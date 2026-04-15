package com.qz.quantumfitzone.data.local.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qz.quantumfitzone.data.local.dao.PersonaDao
import com.qz.quantumfitzone.data.local.dao.MaquinaDao
import com.qz.quantumfitzone.data.local.dao.RutinaDao
import com.qz.quantumfitzone.data.local.dao.EjercicioDao
import com.qz.quantumfitzone.data.local.dao.HistorialEntrenamientoDao
import com.qz.quantumfitzone.data.model.PersonaEntity
import com.qz.quantumfitzone.data.model.MaquinaEntity
import com.qz.quantumfitzone.data.model.RutinaEntity
import com.qz.quantumfitzone.data.model.EjercicioEntity
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity

@Database(
    entities = [
        PersonaEntity::class,
        MaquinaEntity::class,
        RutinaEntity::class,
        EjercicioEntity::class,
        HistorialEntrenamientoEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
    abstract fun maquinaDao(): MaquinaDao
    abstract fun rutinaDao(): RutinaDao
    abstract fun ejercicioDao(): EjercicioDao
    abstract fun historialEntrenamientoDao(): HistorialEntrenamientoDao
}
