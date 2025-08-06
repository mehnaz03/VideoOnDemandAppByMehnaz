package com.mehnaz.videoondemandapp.ui.movielist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.repository.MovieRepository
import kotlin.math.ceil
import android.util.Log

class MoviePagingSource(
    private val repository: MovieRepository,
    private val query: String,
    private val year: String? = null
) : PagingSource<Int, MovieItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieItem> {
        val page = params.key ?: 1
        return try {
            val response = repository.searchMovies(query, page, year)
            val movies = response.Search ?: emptyList()
            val totalResults = response.totalResults?.toIntOrNull() ?: 0
            val totalPages = ceil(totalResults / 10.0).toInt()

            Log.d("MoviePagingSource", "Loaded page $page with ${movies.size} movies")

            LoadResult.Page(
                data = movies,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page >= totalPages || movies.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            Log.e("MoviePagingSource", "Error loading page $page", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieItem>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.let { page ->
                page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
            }
        }
    }
}