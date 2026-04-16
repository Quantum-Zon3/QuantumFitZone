package com.qz.quantumfitzone.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.qz.quantumfitzone.data.local.repository.DatabaseProvider
import com.qz.quantumfitzone.data.model.RutinaEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RoutineListViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DatabaseProvider
        .getDatabase(application)
        .rutinaDao()

    val routines: Flow<List<RutinaEntity>> = DatabaseProvider
        .getDatabase(application)
        .rutinaDao()
        .obtenerTodas()

    fun eliminarRutina(rutina: RutinaEntity) {
        viewModelScope.launch {
            dao.eliminar(rutina)
        }
    }
}
