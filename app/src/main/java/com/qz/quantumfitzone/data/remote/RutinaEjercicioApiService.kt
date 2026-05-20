package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.remote.model.RutinaEjercicioDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RutinaEjercicioApiService {
    @POST("QFZServer/rutina-ejercicios")
    suspend fun crearRutinaEjercicio(@Body rutinaEjercicio: RutinaEjercicioDto): RutinaEjercicioDto

    @GET("QFZServer/rutina-ejercicios")
    suspend fun obtenerRutinaEjercicios(): List<RutinaEjercicioDto>

    @GET("QFZServer/rutina-ejercicios/{id}")
    suspend fun obtenerRutinaEjercicio(@Path("id") id: Int): RutinaEjercicioDto

    @GET("QFZServer/rutina-ejercicios/rutina/{idRutina}")
    suspend fun obtenerRutinaEjerciciosPorRutina(
        @Path("idRutina") idRutina: Int
    ): List<RutinaEjercicioDto>

    @PUT("QFZServer/rutina-ejercicios/{id}")
    suspend fun actualizarRutinaEjercicio(
        @Path("id") id: Int,
        @Body rutinaEjercicio: RutinaEjercicioDto
    ): RutinaEjercicioDto

    @DELETE("QFZServer/rutina-ejercicios/{id}")
    suspend fun eliminarRutinaEjercicio(@Path("id") id: Int)
}
