package com.mehnaz.videoondemandapp.ui.movielist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.databinding.ItemMovieVerticalBinding


class MovieListPagingAdapter(
    private val onItemClick: (MovieItem) -> Unit
) : PagingDataAdapter<MovieItem, MovieListPagingAdapter.MovieViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieItem>() {
            override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem.imdbID == newItem.imdbID
            }

            override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class MovieViewHolder(private val binding: ItemMovieVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieItem) {
            binding.tvTitle.text = movie.Title
            binding.tvYear.text = movie.Year
            Glide.with(binding.root).load(movie.Poster).into(binding.ivPoster)
            binding.root.setOnClickListener { onItemClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}