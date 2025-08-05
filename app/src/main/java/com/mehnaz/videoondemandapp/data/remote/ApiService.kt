package com.mehnaz.videoondemandapp.data.remote

import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") query: String,
        @Query("page") page: Int = 1,
        @Query("y") year: String? = null
    ): MovieResponse

    @GET("/")
    suspend fun getMovieDetails(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String
    ): MovieItem
}