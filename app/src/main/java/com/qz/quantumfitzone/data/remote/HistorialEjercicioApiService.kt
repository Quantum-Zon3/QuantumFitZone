package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.remote.model.HistorialEjercicioDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HistorialEjercicioApiService {
    @POST("QFZServer/historial-ejercicios")
    suspend fun crearHistorialEjercicio(
        @Body historialEjercicio: HistorialEjercicioDto
    ): HistorialEjercicioDto

    @GET("QFZServer/historial-ejercicios")
    suspend fun obtenerHistorialEjercicios(): List<HistorialEjercicioDto>

    @GET("QFZServer/historial-ejercicios/{id}")
    suspend fun obtenerHistorialEjercicio(@Path("id") id: Int): HistorialEjercicioDto

    @GET("QFZServer/historial-ejercicios/historial/{idHistorial}")
    suspend fun obtenerHistorialEjerciciosPorHistorial(
        @Path("idHistorial") idHistorial: Int
    ): List<HistorialEjercicioDto>

    @PUT("QFZServer/historial-ejercicios/{id}")
    suspend fun actualizarHistorialEjercicio(
        @Path("id") id: Int,
        @Body historialEjercicio: HistorialEjercicioDto
    ): HistorialEjercicioDto

    @DELETE("QFZServer/historial-ejercicios/{id}")
    suspend fun eliminarHistorialEjercicio(@Path("id") id: Int)
}
