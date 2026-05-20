package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.remote.model.HistorialEntrenamientoDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface HistorialEntrenamientoApiService {
    @POST("QFZServer/historial-entrenamientos")
    suspend fun crearHistorialEntrenamiento(
        @Body historial: HistorialEntrenamientoDto
    ): HistorialEntrenamientoDto

    @GET("QFZServer/historial-entrenamientos")
    suspend fun obtenerHistorialEntrenamientos(): List<HistorialEntrenamientoDto>

    @GET("QFZServer/historial-entrenamientos/{id}")
    suspend fun obtenerHistorialEntrenamiento(@Path("id") id: Int): HistorialEntrenamientoDto

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
}
