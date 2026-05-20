package com.qz.quantumfitzone.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.MaquinaEntity
import com.qz.quantumfitzone.data.remote.ApiClient
import com.qz.quantumfitzone.data.remote.SessionManager
import com.qz.quantumfitzone.data.remote.model.AuthResponse
import com.qz.quantumfitzone.data.remote.model.LoginBody
import com.qz.quantumfitzone.data.remote.model.RefreshTokenRequest
import com.qz.quantumfitzone.data.remote.model.toDto
import com.qz.quantumfitzone.data.remote.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MaquinaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DatabaseProvider.getDatabase(application).maquinaDao()
    private val sessionManager = SessionManager(application)
    private val authApi = ApiClient.createAuthApi(sessionManager)
    private val maquinaApi = ApiClient.createMaquinaApi(sessionManager)

    val maquinas: Flow<List<MaquinaEntity>> = dao.obtenerTodas()

    var maquinaForm by mutableStateOf(MaquinaEntity(nombre = ""))
        private set

    var isEditing by mutableStateOf(false)
        private set

    var formError by mutableStateOf<String?>(null)
        private set

    var operationError by mutableStateOf<String?>(null)
        private set

    var isSaving by mutableStateOf(false)
        private set

    var isSyncing by mutableStateOf(false)
        private set

    init {
        sincronizarMaquinas()
    }

    fun onNombreChange(valor: String) {
        maquinaForm = maquinaForm.copy(nombre = valor)
    }

    fun onGrupoMuscularChange(valor: String) {
        maquinaForm = maquinaForm.copy(grupo_muscular = valor.ifBlank { null })
    }

    fun onDescripcionChange(valor: String) {
        maquinaForm = maquinaForm.copy(descripcion = valor.ifBlank { null })
    }

    fun onImagenChange(valor: String) {
        maquinaForm = maquinaForm.copy(imagen = valor.ifBlank { null })
    }

    fun prepareCreate() {
        isEditing = false
        formError = null
        maquinaForm = MaquinaEntity(nombre = "")
    }

    fun prepareEdit(maquina: MaquinaEntity) {
        isEditing = true
        formError = null
        maquinaForm = maquina
    }

    fun clearForm() {
        isEditing = false
        formError = null
        maquinaForm = MaquinaEntity(nombre = "")
    }

    fun clearOperationError() {
        operationError = null
    }

    fun guardar(onSuccess: () -> Unit) {
        val nombreLimpio = maquinaForm.nombre.trim()
        if (nombreLimpio.isBlank()) {
            formError = "El nombre de la maquina es obligatorio."
            return
        }

        val maquinaLimpia = maquinaForm.copy(
            nombre = nombreLimpio,
            grupo_muscular = maquinaForm.grupo_muscular?.trim()?.ifBlank { null },
            descripcion = maquinaForm.descripcion?.trim()?.ifBlank { null },
            imagen = maquinaForm.imagen?.trim()?.ifBlank { null }
        )

        viewModelScope.launch {
            isSaving = true
            val resultado = runCatching {
                ejecutarConSesionActiva {
                    if (isEditing) {
                        maquinaApi.actualizarMaquina(maquinaLimpia.id_maquina, maquinaLimpia.toDto()).toEntity()
                    } else {
                        maquinaApi.crearMaquina(maquinaLimpia.toDto()).toEntity()
                    }
                }
            }
            isSaving = false

            resultado
                .onSuccess { guardada ->
                    dao.insertar(guardada)
                    operationError = null
                    clearForm()
                    onSuccess()
                }
                .onFailure { error ->
                    formError = error.toOperationMessage("guardar la maquina")
                }
        }
    }

    fun eliminar(maquina: MaquinaEntity) {
        viewModelScope.launch {
            val resultado = runCatching {
                ejecutarConSesionActiva {
                    maquinaApi.eliminarMaquina(maquina.id_maquina)
                }
            }

            resultado
                .onSuccess {
                    dao.eliminar(maquina)
                    operationError = null
                }
                .onFailure { error ->
                    operationError = error.toOperationMessage("eliminar la maquina")
                }
        }
    }

    private fun sincronizarMaquinas() {
        viewModelScope.launch {
            isSyncing = true
            val resultado = runCatching {
                ejecutarConSesionActiva {
                    maquinaApi.obtenerMaquinas()
                }
            }
            isSyncing = false

            resultado
                .onSuccess { remotas ->
                    dao.eliminarTodas()
                    remotas.forEach { dao.insertar(it.toEntity()) }
                    operationError = null
                }
                .onFailure { error ->
                    operationError = error.toOperationMessage("sincronizar las maquinas")
                }
        }
    }

    private suspend fun <T> ejecutarConSesionActiva(block: suspend () -> T): T {
        if (sessionManager.getAccessToken().isBlank()) {
            renovarSesion()
        }

        return try {
            block()
        } catch (error: HttpException) {
            if (error.code() == 401 || error.code() == 403) {
                renovarSesion()
                block()
            } else {
                throw error
            }
        }
    }

    private suspend fun renovarSesion() {
        val refreshToken = sessionManager.getRefreshToken()
        if (refreshToken.isNotBlank()) {
            val authResponse = runCatching {
                authApi.refreshToken(RefreshTokenRequest(refreshToken))
            }.getOrNull()

            if (authResponse != null) {
                guardarSesion(authResponse)
                return
            }
        }

        val usuario = sessionManager.getUser()
        val pass = sessionManager.getPassword()
        if (usuario.isNotBlank() && pass.isNotBlank()) {
            val authResponse = authApi.login(LoginBody(correo = usuario, password = pass))
            guardarSesion(authResponse)
            return
        }

        throw IllegalStateException("No hay una sesion valida. Inicia sesion nuevamente.")
    }

    private fun guardarSesion(authResponse: AuthResponse) {
        sessionManager.saveSession(
            user = authResponse.persona.correo,
            password = sessionManager.getPassword(),
            accessToken = authResponse.accessToken,
            refreshToken = authResponse.refreshToken
        )
    }

    private fun Throwable.toOperationMessage(action: String): String {
        return when (this) {
            is HttpException -> when (code()) {
                401, 403 -> "No fue posible $action: la sesion expiro o el token no fue aceptado."
                404 -> "No fue posible $action: el recurso no existe en el servidor."
                409 -> "No fue posible $action: ya existe un registro con esos datos."
                else -> "No fue posible $action. Respuesta del servidor: HTTP ${code()}."
            }
            is IOException -> "No fue posible $action. Verifica que QuantumFitZoneServer este encendido y que la IP de BASE_URL sea correcta."
            else -> message ?: "No fue posible $action."
        }
    }
}
