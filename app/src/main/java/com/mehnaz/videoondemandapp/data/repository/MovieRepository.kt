package com.mehnaz.videoondemandapp.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mehnaz.videoondemandapp.BuildConfig

import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.model.MovieResponse
import com.mehnaz.videoondemandapp.data.remote.ApiService
import com.mehnaz.videoondemandapp.ui.movielist.MoviePagingSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val apiService: ApiService  // ApiService instance injected for making network calls
) {
    // API key for authenticating requests to the movie API
   // private val apiKey = "f42caa44"
    private val apiKey =BuildConfig.OMDB_API_KEY
    init {
        Log.e("API_KEY", "OMDB Key: '$apiKey'")
    }
    /**
     * Suspend function to search movies by query, page number, and optional year filter.
     * Calls the ApiService to fetch MovieResponse.
     */
    suspend fun searchMovies(query: String, page: Int, year: String? = null): MovieResponse {
        return apiService.searchMovies(apiKey, query, page, year)
    }

    /**
     * Suspend function to get detailed info of a movie by IMDb ID.
     * Returns a MovieItem representing detailed info.
     */
    suspend fun getMovieDetails(imdbId: String): MovieItem {
        return apiService.getMovieDetails(apiKey, imdbId)
    }

    /**
     * Returns a Flow of PagingData<MovieItem> for paginated movie listing.
     * Uses the MoviePagingSource to load data page by page.
     * Supports optional query and year filtering.
     */
    fun getMovieList(query: String, year: String? = null): Flow<PagingData<MovieItem>> {
        return Pager(
            config = PagingConfig(pageSize = 10)  // Configure pages of size 10 items
        ) {
            MoviePagingSource(this, query, year) // PagingSource instance for fetching data
        }.flow  // Return as Flow for collection in ViewModel/Fragment
    }
}
