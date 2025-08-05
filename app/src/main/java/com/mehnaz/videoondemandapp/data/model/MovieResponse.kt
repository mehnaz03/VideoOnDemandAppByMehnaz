package com.mehnaz.videoondemandapp.data.model

data class MovieResponse(
    val Search: List<MovieItem>?,
    val totalResults: String?,
    val Response: String?,
    val Error: String?
)