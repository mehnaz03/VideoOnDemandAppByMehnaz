package com.mehnaz.videoondemandapp.data.repository

import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.model.MovieResponse
import com.mehnaz.videoondemandapp.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MovieRepository @Inject constructor(
    private val apiService: ApiService
) {
    private val apiKey = "f42caa44"

    suspend fun searchMovies(query: String, page: Int, year: String? = null): MovieResponse {
        return apiService.searchMovies(apiKey, query, page, year)
    }

    suspend fun getMovieDetails(imdbId: String): MovieItem {
        return apiService.getMovieDetails(apiKey, imdbId)
    }
}