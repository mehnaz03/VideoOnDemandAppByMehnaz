package com.mehnaz.videoondemandapp.ui.movielist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.databinding.ItemMovieListBinding


class MovieListPagingAdapter(
    private val onItemClick: (MovieItem) -> Unit // Callback when an item is clicked
) : PagingDataAdapter<MovieItem, MovieListPagingAdapter.MovieViewHolder>(DIFF_CALLBACK) {

    companion object {
        // DiffUtil helps RecyclerView update only changed items instead of the whole list
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieItem>() {
            override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                // Compare unique IDs to check if two items are the same
                return oldItem.imdbID == newItem.imdbID
            }

            override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                // Compare entire objects to check if their content is the same
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        // Can be customized if you have multiple view types
        return super.getItemViewType(position)
    }

    // ViewHolder for movie list item
    inner class MovieViewHolder(private val binding: ItemMovieListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind data to the views
        fun bind(movie: MovieItem) {
            binding.tvTitle.text = movie.Title
            binding.tvYear.text = movie.Year

            // Load poster image using Glide
            Glide.with(binding.root)
                .load(movie.Poster)
                .into(binding.ivPoster)

            // Handle item click
            binding.root.setOnClickListener { onItemClick(movie) }
        }
    }

    // Create new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    // Bind data to the ViewHolder
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}
