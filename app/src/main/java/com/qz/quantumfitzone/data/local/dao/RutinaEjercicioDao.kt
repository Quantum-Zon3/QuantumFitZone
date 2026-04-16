package com.qz.quantumfitzone.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.qz.quantumfitzone.data.model.RutinaEjercicioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaEjercicioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(asignacion: RutinaEjercicioEntity)

    @Query("DELETE FROM rutina_ejercicios WHERE id_rutina = :idRutina")
    suspend fun eliminarPorRutina(idRutina: Int)

    @Query("SELECT * FROM rutina_ejercicios WHERE id_rutina = :idRutina ORDER BY orden ASC")
    fun obtenerPorRutina(idRutina: Int): Flow<List<RutinaEjercicioEntity>>

    @Query("SELECT * FROM rutina_ejercicios WHERE id_rutina = :idRutina ORDER BY orden ASC")
    suspend fun obtenerPorRutinaLista(idRutina: Int): List<RutinaEjercicioEntity>
}
