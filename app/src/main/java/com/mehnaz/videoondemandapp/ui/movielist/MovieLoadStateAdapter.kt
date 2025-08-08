package com.mehnaz.videoondemandapp.ui.movielist



import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mehnaz.videoondemandapp.R
import com.mehnaz.videoondemandapp.databinding.ItemLoadStateBinding
class MovieLoadStateAdapter(
    private val retry: () -> Unit
) : RecyclerView.Adapter<MovieLoadStateAdapter.LoadStateViewHolder>() {

    var loadState: LoadState = LoadState.NotLoading(endOfPaginationReached = false)
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoadStateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_load_state, parent, false)
        return LoadStateViewHolder(view, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, position: Int) {
        holder.bind(loadState)
    }

    override fun getItemCount(): Int = 1

    class LoadStateViewHolder(itemView: View, retry: () -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
    private val errorMsg: TextView = itemView.findViewById(R.id.errorMsg)
    private val retryButton: Button = itemView.findViewById(R.id.retryButton)
        private val loadingText:TextView = itemView.findViewById(R.id.loadingText)

        init {
            retryButton.setOnClickListener { retry() }
        }

        fun bind(loadState: LoadState) {
            progressBar.isVisible = loadState is LoadState.Loading
            loadingText.isVisible = loadState is LoadState.Loading
            errorMsg.isVisible = loadState is LoadState.Error
            retryButton.isVisible = loadState is LoadState.Error

            if (loadState is LoadState.Error) {
                errorMsg.text = loadState.error.localizedMessage
            }
        }
    }
}
