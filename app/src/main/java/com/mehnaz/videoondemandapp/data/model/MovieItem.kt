package com.mehnaz.videoondemandapp.data.model



data class MovieItem(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String,
    val Genre: String? = null,
    val Plot: String? = null,
    val Runtime: String? = null
)
