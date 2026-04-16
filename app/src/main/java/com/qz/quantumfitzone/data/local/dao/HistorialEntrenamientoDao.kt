package com.qz.quantumfitzone.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistorialEntrenamientoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(historial: HistorialEntrenamientoEntity): Long

    @Query(
        """
        SELECT * FROM historial_entrenamientos
        WHERE correo_usuario = :correoUsuario AND completado = 1
        ORDER BY fecha DESC, id_historial DESC
        """
    )
    fun obtenerPorUsuario(correoUsuario: String): Flow<List<HistorialEntrenamientoEntity>>

    @Query(
        """
        SELECT * FROM historial_entrenamientos
        WHERE correo_usuario = :correoUsuario AND en_progreso = 1
        ORDER BY id_historial DESC
        LIMIT 1
        """
    )
    fun obtenerSesionActivaPorUsuario(correoUsuario: String): Flow<HistorialEntrenamientoEntity?>

    @Query(
        """
        SELECT * FROM historial_entrenamientos
        WHERE correo_usuario = :correoUsuario AND en_progreso = 1
        ORDER BY id_historial DESC
        LIMIT 1
        """
    )
    suspend fun obtenerSesionActivaActual(correoUsuario: String): HistorialEntrenamientoEntity?

    @Query("SELECT * FROM historial_entrenamientos WHERE id_historial = :id")
    suspend fun obtenerPorId(id: Int): HistorialEntrenamientoEntity?

    @Update
    suspend fun actualizar(historial: HistorialEntrenamientoEntity)

    @Delete
    suspend fun eliminar(historial: HistorialEntrenamientoEntity)
}
