package com.mehnaz.videoondemandapp.ui.movielist



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mehnaz.videoondemandapp.databinding.ItemLoadStateBinding



class MovieLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<MovieLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(private val binding: ItemLoadStateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRetry.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            binding.progressBar.visibility = if (loadState is LoadState.Loading) View.VISIBLE else View.GONE
            binding.btnRetry.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            binding.tvError.visibility = if (loadState is LoadState.Error) View.VISIBLE else View.GONE
            if (loadState is LoadState.Error) {
                binding.tvError.text = loadState.error.localizedMessage
            }
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = ItemLoadStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }
}
