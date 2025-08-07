package com.mehnaz.videoondemandapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.mehnaz.videoondemandapp.R
import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.databinding.ItemMovieHomeBinding

class HomeMovieAdapter(
    private val onItemClick: (MovieItem) -> Unit
) : ListAdapter<MovieItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MovieItem>() {
            override fun areItemsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem.imdbID == newItem.imdbID
            }

            override fun areContentsTheSame(oldItem: MovieItem, newItem: MovieItem): Boolean {
                return oldItem == newItem
            }
        }

        private const val TYPE_SHIMMER = 0
        private const val TYPE_ITEM = 1
    }

    private var isLoading = true
    private val shimmerItemCount = 6

    fun showShimmerLoading(state: Boolean) {
        isLoading = state
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val binding: ItemMovieHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(movie: MovieItem) {
            binding.tvTitle.text = movie.Title
            Glide.with(binding.root)
                .load(movie.Poster)
                .placeholder(R.drawable.bg_image_placeholder)
                .into(binding.ivPoster)

            binding.root.setOnClickListener { onItemClick(movie) }
        }
    }

    inner class ShimmerViewHolder(binding: View) : RecyclerView.ViewHolder(binding)

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) TYPE_SHIMMER else TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return if (isLoading) shimmerItemCount else super.getItemCount()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val binding = ItemMovieHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MovieViewHolder(binding)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_shimmer_movie, parent, false)
            ShimmerViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MovieViewHolder && !isLoading) {
            holder.bind(getItem(position))
        }
    }
}
