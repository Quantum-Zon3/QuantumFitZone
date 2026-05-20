package com.qz.quantumfitzone.data.remote.model

import com.qz.quantumfitzone.data.model.ExerciseCatalogEntity
import com.qz.quantumfitzone.data.model.HistorialEjercicioEntity
import com.qz.quantumfitzone.data.model.HistorialEntrenamientoEntity
import com.qz.quantumfitzone.data.model.MaquinaEntity
import com.qz.quantumfitzone.data.model.RutinaEjercicioEntity
import com.qz.quantumfitzone.data.model.RutinaEntity

fun MaquinaDto.toEntity(): MaquinaEntity = MaquinaEntity(
    id_maquina = idMaquina,
    nombre = nombre,
    grupo_muscular = grupoMuscular,
    descripcion = descripcion,
    imagen = imagen
)

fun MaquinaEntity.toDto(): MaquinaDto = MaquinaDto(
    idMaquina = id_maquina,
    nombre = nombre,
    grupoMuscular = grupo_muscular,
    descripcion = descripcion,
    imagen = imagen
)

fun ExerciseCatalogDto.toEntity(): ExerciseCatalogEntity = ExerciseCatalogEntity(
    id_exercise = idExercise,
    nombre = nombre,
    descripcion = descripcion,
    grupo_muscular = grupoMuscular,
    id_maquina = idMaquina,
    series = series,
    repeticiones = repeticiones
)

fun ExerciseCatalogEntity.toDto(): ExerciseCatalogDto = ExerciseCatalogDto(
    idExercise = id_exercise,
    nombre = nombre,
    descripcion = descripcion,
    grupoMuscular = grupo_muscular,
    idMaquina = id_maquina,
    series = series,
    repeticiones = repeticiones
)

fun RutinaDto.toEntity(): RutinaEntity = RutinaEntity(
    id_rutina = idRutina,
    nombre = nombre,
    categoria = categoria,
    descanso_segundos = descansoSegundos,
    id_usuario = idUsuario
)

fun RutinaEntity.toDto(): RutinaDto = RutinaDto(
    idRutina = id_rutina,
    nombre = nombre,
    categoria = categoria,
    descansoSegundos = descanso_segundos,
    idUsuario = id_usuario
)

fun RutinaEjercicioDto.toEntity(): RutinaEjercicioEntity = RutinaEjercicioEntity(
    id_rutina_ejercicio = idRutinaEjercicio,
    id_rutina = idRutina,
    id_exercise = idExercise,
    orden = orden,
    peso_actual = pesoActual,
    peso_objetivo = pesoObjetivo
)

fun RutinaEjercicioEntity.toDto(): RutinaEjercicioDto = RutinaEjercicioDto(
    idRutinaEjercicio = id_rutina_ejercicio,
    idRutina = id_rutina,
    idExercise = id_exercise,
    orden = orden,
    pesoActual = peso_actual,
    pesoObjetivo = peso_objetivo
)

fun HistorialEntrenamientoDto.toEntity(): HistorialEntrenamientoEntity = HistorialEntrenamientoEntity(
    id_historial = idHistorial,
    correo_usuario = correoUsuario,
    id_rutina = idRutina,
    fecha = fecha,
    fecha_inicio = fechaInicio,
    fecha_fin = fechaFin,
    titulo = titulo,
    duracion_minutos = duracionMinutos,
    duracion_segundos = duracionSegundos,
    kcal = kcal,
    categoria = categoria,
    en_progreso = enProgreso,
    completado = completado
)

fun HistorialEntrenamientoEntity.toDto(): HistorialEntrenamientoDto = HistorialEntrenamientoDto(
    idHistorial = id_historial,
    correoUsuario = correo_usuario,
    idRutina = id_rutina,
    fecha = fecha,
    fechaInicio = fecha_inicio,
    fechaFin = fecha_fin,
    titulo = titulo,
    duracionMinutos = duracion_minutos,
    duracionSegundos = duracion_segundos,
    kcal = kcal,
    categoria = categoria,
    enProgreso = en_progreso,
    completado = completado
)

fun HistorialEjercicioDto.toEntity(): HistorialEjercicioEntity = HistorialEjercicioEntity(
    id_historial_ejercicio = idHistorialEjercicio,
    id_historial = idHistorial,
    id_rutina = idRutina,
    id_exercise = idExercise,
    correo_usuario = correoUsuario,
    fecha = fecha,
    orden = orden,
    nombre_ejercicio = nombreEjercicio,
    series_objetivo = seriesObjetivo,
    repeticiones_objetivo = repeticionesObjetivo,
    peso_objetivo = pesoObjetivo,
    series_realizadas = seriesRealizadas,
    repeticiones_realizadas = repeticionesRealizadas,
    peso_realizado = pesoRealizado,
    completado = completado
)

fun HistorialEjercicioEntity.toDto(): HistorialEjercicioDto = HistorialEjercicioDto(
    idHistorialEjercicio = id_historial_ejercicio,
    idHistorial = id_historial,
    idRutina = id_rutina,
    idExercise = id_exercise,
    correoUsuario = correo_usuario,
    fecha = fecha,
    orden = orden,
    nombreEjercicio = nombre_ejercicio,
    seriesObjetivo = series_objetivo,
    repeticionesObjetivo = repeticiones_objetivo,
    pesoObjetivo = peso_objetivo,
    seriesRealizadas = series_realizadas,
    repeticionesRealizadas = repeticiones_realizadas,
    pesoRealizado = peso_realizado,
    completado = completado
)
