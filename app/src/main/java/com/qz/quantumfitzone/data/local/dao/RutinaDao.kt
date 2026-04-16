package com.qz.quantumfitzone.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qz.quantumfitzone.data.model.RutinaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(rutina: RutinaEntity): Long

    @Query("SELECT * FROM rutinas")
    fun obtenerTodas(): Flow<List<RutinaEntity>>

    @Query("SELECT * FROM rutinas WHERE id_rutina = :id")
    suspend fun obtenerPorId(id: Int): RutinaEntity?

    @Query("SELECT * FROM rutinas WHERE id_usuario = :idUsuario")
    fun obtenerPorUsuario(idUsuario: Int): Flow<List<RutinaEntity>>

    @Update
    suspend fun actualizar(rutina: RutinaEntity)

    @Delete
    suspend fun eliminar(rutina: RutinaEntity)
}
