package com.qz.quantumfitzone.viewModel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.MaquinaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MaquinaViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DatabaseProvider
        .getDatabase(application)
        .maquinaDao()

    val maquinas: Flow<List<MaquinaEntity>> = dao.obtenerTodas()

    var maquinaForm by mutableStateOf(MaquinaEntity(nombre = ""))
        private set

    var isEditing by mutableStateOf(false)
        private set

    var formError by mutableStateOf<String?>(null)
        private set

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
            if (isEditing) {
                dao.actualizar(maquinaLimpia)
            } else {
                dao.insertar(maquinaLimpia)
            }
            clearForm()
            onSuccess()
        }
    }

    fun eliminar(maquina: MaquinaEntity) {
        viewModelScope.launch {
            dao.eliminar(maquina)
        }
    }
}
