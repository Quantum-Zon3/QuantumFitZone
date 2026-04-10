package com.qz.quantumfitzone.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.qz.quantumfitzone.data.model.PersonaEntity

@Dao
interface PersonaDao {
    // Crear / Insertar
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(persona: PersonaEntity)

    // Leer todos (observables en tiempo real)
    @Query("SELECT * FROM personas")
    fun obtenerTodas(): kotlinx.coroutines.flow.Flow<List<PersonaEntity>>

    // Leer todos (no observables)
    @Query("SELECT * FROM personas")
    suspend fun obtenerTodos(): List<PersonaEntity>


    // Leer por correo (clave primaria)
    @Query("SELECT * FROM personas WHERE correo = :correo")
    suspend fun obtenerPorCorreo(correo: String): PersonaEntity?

    // Actualizar un registro existente
    @Update
    suspend fun actualizar(persona: PersonaEntity)

    // Eliminar un registro específico
    @Delete
    suspend fun eliminar(persona: PersonaEntity)

    // Eliminar todos los registros
    @Query("DELETE FROM personas")
    suspend fun eliminarTodos()
}