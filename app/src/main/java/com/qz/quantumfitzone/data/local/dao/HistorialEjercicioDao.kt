package com.qz.quantumfitzone.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qz.quantumfitzone.data.model.HistorialEjercicioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialEjercicioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(historialEjercicio: HistorialEjercicioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(historialEjercicios: List<HistorialEjercicioEntity>)

    @Query(
        """
        SELECT * FROM historial_ejercicios
        WHERE id_historial = :idHistorial
        ORDER BY orden ASC, id_historial_ejercicio ASC
        """
    )
    fun obtenerPorHistorial(idHistorial: Int): Flow<List<HistorialEjercicioEntity>>

    @Query(
        """
        SELECT * FROM historial_ejercicios
        WHERE correo_usuario = :correoUsuario AND id_exercise = :idExercise
        ORDER BY fecha DESC, id_historial_ejercicio DESC
        """
    )
    fun obtenerPorUsuarioYEjercicio(
        correoUsuario: String,
        idExercise: Int
    ): Flow<List<HistorialEjercicioEntity>>

    @Query("SELECT * FROM historial_ejercicios WHERE id_historial_ejercicio = :id")
    suspend fun obtenerPorId(id: Int): HistorialEjercicioEntity?

    @Update
    suspend fun actualizar(historialEjercicio: HistorialEjercicioEntity)

    @Delete
    suspend fun eliminar(historialEjercicio: HistorialEjercicioEntity)
}
