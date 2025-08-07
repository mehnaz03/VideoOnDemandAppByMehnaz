package com.mehnaz.videoondemandapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.repository.MovieRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class HomeViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _batmanMovies = MutableLiveData<List<MovieItem>>()
    val batmanMovies: LiveData<List<MovieItem>> = _batmanMovies

    private val _latestMovies = MutableLiveData<List<MovieItem>>()
    val latestMovies: LiveData<List<MovieItem>> = _latestMovies

    private val _bannerMovies = MutableLiveData<List<MovieItem>>()
    val bannerMovies: LiveData<List<MovieItem>> = _bannerMovies

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun fetchHomeData() {
        viewModelScope.launch {
            try {
                val batmanResponse = repository.searchMovies("Batman", 1)
                _batmanMovies.value = batmanResponse.Search ?: emptyList()

                val latestResponse = repository.searchMovies("movie", 1, year = "2022")
                _latestMovies.value = latestResponse.Search ?: emptyList()

                _bannerMovies.value = batmanResponse.Search?.take(5) ?: emptyList() // Use top 5
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown Error"
            }
        }
    }
}