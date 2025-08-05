package com.mehnaz.videoondemandapp.data.repository

import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.model.MovieResponse
import com.mehnaz.videoondemandapp.data.remote.RetrofitInstance


class MovieRepository(private val apiKey: String) {

    suspend fun searchMovies(query: String, page: Int, year: String? = null): MovieResponse {
        return RetrofitInstance.api.searchMovies(apiKey, query, page, year)
    }

    suspend fun getMovieDetails(imdbId: String): MovieItem {
        return RetrofitInstance.api.getMovieDetails(apiKey, imdbId)
    }
}