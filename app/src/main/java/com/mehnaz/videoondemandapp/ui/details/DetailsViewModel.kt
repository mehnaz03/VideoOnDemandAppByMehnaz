package com.mehnaz.videoondemandapp.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.repository.MovieRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movieDetail = MutableLiveData<MovieItem>()
    val movieDetail: LiveData<MovieItem> = _movieDetail
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

fun loadMovieDetails(imdbId: String) {
    viewModelScope.launch {
        try {
            val detail = repository.getMovieDetails(imdbId)
            _movieDetail.value = detail
        } catch (e: Exception) {
            // Set error message, can be shown in UI
            _error.value = e.localizedMessage ?: "Failed to load movie details"
        }
    }
}
}