package com.mehnaz.videoondemandapp.ui.movielist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.repository.MovieRepository
import kotlin.math.ceil
import android.util.Log

class MoviePagingSource(
    private val repository: MovieRepository, // Repository for fetching movie data
    private val query: String,               // Search query for movies
    private val year: String? = null          // Optional year filter
) : PagingSource<Int, MovieItem>() {          // PagingSource with page key type Int and data type MovieItem

    // Called to load a page of data
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieItem> {
        val page = params.key ?: 1 // If no key is provided, start from page 1

        return try {
            // Fetch movie data from repository
            val response = repository.searchMovies(query, page, year)

            // Get movie list, or an empty list if null
            val movies = response.Search ?: emptyList()

            // Total results from API (if available), otherwise 0
            val totalResults = response.totalResults?.toIntOrNull() ?: 0

            // Calculate total number of pages (API returns 10 items per page)
            val totalPages = ceil(totalResults / 10.0).toInt()

            // Debug log for tracking
            Log.d("MoviePagingSource", "Loaded page $page with ${movies.size} movies")

            // Return a successful page result
            LoadResult.Page(
                data = movies,                             // The list of movies
                prevKey = if (page == 1) null else page - 1, // Previous page key (null if first page)
                nextKey = if (page >= totalPages || movies.isEmpty()) null else page + 1 // Next page key (null if last page or no data)
            )

        } catch (e: Exception) {
            // Log and return error if something goes wrong
            Log.e("MoviePagingSource", "Error loading page $page", e)
            LoadResult.Error(e)
        }
    }

    // Determines the key to be used when refreshing data
    override fun getRefreshKey(state: PagingState<Int, MovieItem>): Int? {
        return state.anchorPosition?.let { position -> // Anchor position is the last accessed index
            state.closestPageToPosition(position)?.let { page ->
                // Return the previous page +1 or the next page -1
                page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
            }
        }
    }
}
