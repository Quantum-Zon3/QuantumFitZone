package com.qz.quantumfitzone.data.remote

import com.qz.quantumfitzone.data.remote.model.ExerciseCatalogDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ExerciseCatalogApiService {
    @POST("QFZServer/exercise-catalog")
    suspend fun crearExerciseCatalog(@Body ejercicio: ExerciseCatalogDto): ExerciseCatalogDto

    @GET("QFZServer/exercise-catalog")
    suspend fun obtenerExerciseCatalog(): List<ExerciseCatalogDto>

    @GET("QFZServer/exercise-catalog/{id}")
    suspend fun obtenerExerciseCatalogPorId(@Path("id") id: Int): ExerciseCatalogDto

    @GET("QFZServer/exercise-catalog/grupo/{grupoMuscular}")
    suspend fun obtenerExerciseCatalogPorGrupo(
        @Path("grupoMuscular") grupoMuscular: String
    ): List<ExerciseCatalogDto>

    @PUT("QFZServer/exercise-catalog/{id}")
    suspend fun actualizarExerciseCatalog(
        @Path("id") id: Int,
        @Body ejercicio: ExerciseCatalogDto
    ): ExerciseCatalogDto

    @DELETE("QFZServer/exercise-catalog/{id}")
    suspend fun eliminarExerciseCatalog(@Path("id") id: Int)
}
