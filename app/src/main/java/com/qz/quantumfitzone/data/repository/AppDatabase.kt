package com.qz.quantumfitzone.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qz.quantumfitzone.data.dao.PersonaDao
import com.qz.quantumfitzone.data.dao.MaquinaDao
import com.qz.quantumfitzone.data.dao.RutinaDao
import com.qz.quantumfitzone.data.dao.EjercicioDao
import com.qz.quantumfitzone.data.model.PersonaEntity
import com.qz.quantumfitzone.data.model.MaquinaEntity
import com.qz.quantumfitzone.data.model.RutinaEntity
import com.qz.quantumfitzone.data.model.EjercicioEntity

@Database(
    entities = [
        PersonaEntity::class,
        MaquinaEntity::class,
        RutinaEntity::class,
        EjercicioEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun personaDao(): PersonaDao
    abstract fun maquinaDao(): MaquinaDao
    abstract fun rutinaDao(): RutinaDao
    abstract fun ejercicioDao(): EjercicioDao
}
