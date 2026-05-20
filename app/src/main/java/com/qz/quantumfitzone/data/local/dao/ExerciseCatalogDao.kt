package com.qz.quantumfitzone.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qz.quantumfitzone.data.model.ExerciseCatalogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseCatalogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(ejercicio: ExerciseCatalogEntity): Long

    @Query("SELECT * FROM exercise_catalog ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<ExerciseCatalogEntity>>

    @Query("SELECT * FROM exercise_catalog WHERE id_exercise = :id")
    suspend fun obtenerPorId(id: Int): ExerciseCatalogEntity?

    @Update
    suspend fun actualizar(ejercicio: ExerciseCatalogEntity)

    @Delete
    suspend fun eliminar(ejercicio: ExerciseCatalogEntity)

    @Query("DELETE FROM exercise_catalog")
    suspend fun eliminarTodos()
}
