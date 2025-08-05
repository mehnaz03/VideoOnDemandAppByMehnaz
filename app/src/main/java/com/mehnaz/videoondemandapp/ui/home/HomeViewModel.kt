package com.mehnaz.videoondemandapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.repository.MovieRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _batmanMovies = MutableLiveData<List<MovieItem>>()
    val batmanMovies: LiveData<List<MovieItem>> = _batmanMovies

    private val _latestMovies = MutableLiveData<List<MovieItem>>()
    val latestMovies: LiveData<List<MovieItem>> = _latestMovies

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchHomeData() {
        viewModelScope.launch {
            try {
                val batmanResponse = repository.searchMovies("Batman", 1)
                _batmanMovies.value = batmanResponse.Search ?: emptyList()

                val latestResponse = repository.searchMovies("movie", 1, year = "2022")
                _latestMovies.value = latestResponse.Search ?: emptyList()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown Error"
            }
        }
    }
}