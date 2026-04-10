package com.qz.quantumfitzone.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qz.quantumfitzone.data.model.MaquinaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaquinaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(maquina: MaquinaEntity)

    @Query("SELECT * FROM maquinas")
    fun obtenerTodas(): Flow<List<MaquinaEntity>>

    @Query("SELECT * FROM maquinas WHERE id_maquina = :id")
    suspend fun obtenerPorId(id: Int): MaquinaEntity?

    @Update
    suspend fun actualizar(maquina: MaquinaEntity)

    @Delete
    suspend fun eliminar(maquina: MaquinaEntity)
}
