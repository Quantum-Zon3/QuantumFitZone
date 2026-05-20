package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.model.PersonaEntity
import com.qz.quantumfitzone.data.remote.model.AuthResponse
import com.qz.quantumfitzone.data.remote.model.ExerciseCatalogDto
import com.qz.quantumfitzone.data.remote.model.HistorialEjercicioDto
import com.qz.quantumfitzone.data.remote.model.HistorialEntrenamientoDto
import com.qz.quantumfitzone.data.remote.model.LoginBody
import com.qz.quantumfitzone.data.remote.model.MaquinaDto
import com.qz.quantumfitzone.data.remote.model.RutinaDto
import com.qz.quantumfitzone.data.remote.model.RutinaEjercicioDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PersonaApiService {
    @POST("QFZServer/login")
    suspend fun login(@Body body: LoginBody): AuthResponse

    @POST("QFZServer/personas")
    suspend fun registrar(@Body persona: PersonaEntity): PersonaEntity

    @GET("QFZServer/personas")
    suspend fun obtenerPersonas(): List<PersonaEntity>

    @GET("QFZServer/personas/{correo}")
    suspend fun obtenerPersona(@Path("correo") correo: String): PersonaEntity

    @PUT("QFZServer/personas/{correo}")
    suspend fun actualizarPersona(
        @Path("correo") correo: String,
        @Body persona: PersonaEntity
    ): PersonaEntity

    @DELETE("QFZServer/personas/{correo}")
    suspend fun eliminarPersona(@Path("correo") correo: String)

    @POST("QFZServer/maquinas")
    suspend fun crearMaquina(@Body maquina: MaquinaDto): MaquinaDto

    @GET("QFZServer/maquinas")
    suspend fun obtenerMaquinas(): List<MaquinaDto>

    @PUT("QFZServer/maquinas/{id}")
    suspend fun actualizarMaquina(@Path("id") id: Int, @Body maquina: MaquinaDto): MaquinaDto

    @DELETE("QFZServer/maquinas/{id}")
    suspend fun eliminarMaquina(@Path("id") id: Int)

    @POST("QFZServer/exercise-catalog")
    suspend fun crearExerciseCatalog(@Body ejercicio: ExerciseCatalogDto): ExerciseCatalogDto

    @GET("QFZServer/exercise-catalog")
    suspend fun obtenerExerciseCatalog(): List<ExerciseCatalogDto>

    @PUT("QFZServer/exercise-catalog/{id}")
    suspend fun actualizarExerciseCatalog(
        @Path("id") id: Int,
        @Body ejercicio: ExerciseCatalogDto
    ): ExerciseCatalogDto

    @DELETE("QFZServer/exercise-catalog/{id}")
    suspend fun eliminarExerciseCatalog(@Path("id") id: Int)

    @POST("QFZServer/rutinas")
    suspend fun crearRutina(@Body rutina: RutinaDto): RutinaDto

    @GET("QFZServer/rutinas")
    suspend fun obtenerRutinas(): List<RutinaDto>

    @GET("QFZServer/rutinas/{id}")
    suspend fun obtenerRutina(@Path("id") id: Int): RutinaDto

    @PUT("QFZServer/rutinas/{id}")
    suspend fun actualizarRutina(@Path("id") id: Int, @Body rutina: RutinaDto): RutinaDto

    @DELETE("QFZServer/rutinas/{id}")
    suspend fun eliminarRutina(@Path("id") id: Int)

    @POST("QFZServer/rutina-ejercicios")
    suspend fun crearRutinaEjercicio(@Body rutinaEjercicio: RutinaEjercicioDto): RutinaEjercicioDto

    @GET("QFZServer/rutina-ejercicios/rutina/{idRutina}")
    suspend fun obtenerRutinaEjerciciosPorRutina(
        @Path("idRutina") idRutina: Int
    ): List<RutinaEjercicioDto>

    @DELETE("QFZServer/rutina-ejercicios/{id}")
    suspend fun eliminarRutinaEjercicio(@Path("id") id: Int)

    @POST("QFZServer/historial-entrenamientos")
    suspend fun crearHistorialEntrenamiento(
        @Body historial: HistorialEntrenamientoDto
    ): HistorialEntrenamientoDto

    @GET("QFZServer/historial-entrenamientos/usuario/{correo}")
    suspend fun obtenerHistorialEntrenamientosPorUsuario(
        @Path("correo") correo: String
    ): List<HistorialEntrenamientoDto>

    @PUT("QFZServer/historial-entrenamientos/{id}")
    suspend fun actualizarHistorialEntrenamiento(
        @Path("id") id: Int,
        @Body historial: HistorialEntrenamientoDto
    ): HistorialEntrenamientoDto

    @DELETE("QFZServer/historial-entrenamientos/{id}")
    suspend fun eliminarHistorialEntrenamiento(@Path("id") id: Int)

    @POST("QFZServer/historial-ejercicios")
    suspend fun crearHistorialEjercicio(
        @Body historialEjercicio: HistorialEjercicioDto
    ): HistorialEjercicioDto

    @GET("QFZServer/historial-ejercicios/historial/{idHistorial}")
    suspend fun obtenerHistorialEjerciciosPorHistorial(
        @Path("idHistorial") idHistorial: Int
    ): List<HistorialEjercicioDto>

    @PUT("QFZServer/historial-ejercicios/{id}")
    suspend fun actualizarHistorialEjercicio(
        @Path("id") id: Int,
        @Body historialEjercicio: HistorialEjercicioDto
    ): HistorialEjercicioDto
}
