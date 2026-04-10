package com.qz.quantumfitzone.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qz.quantumfitzone.data.model.EjercicioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EjercicioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(ejercicio: EjercicioEntity)

    @Query("SELECT * FROM ejercicios")
    fun obtenerTodos(): Flow<List<EjercicioEntity>>

    @Query("SELECT * FROM ejercicios WHERE id_ejercicio = :id")
    suspend fun obtenerPorId(id: Int): EjercicioEntity?

    @Query("SELECT * FROM ejercicios WHERE id_rutina = :idRutina")
    fun obtenerPorRutina(idRutina: Int): Flow<List<EjercicioEntity>>

    @Update
    suspend fun actualizar(ejercicio: EjercicioEntity)

    @Delete
    suspend fun eliminar(ejercicio: EjercicioEntity)
}
