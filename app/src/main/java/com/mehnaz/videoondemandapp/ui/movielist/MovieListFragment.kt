package com.mehnaz.videoondemandapp.ui.movielist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.mehnaz.videoondemandapp.utils.NetworkStatusLiveData
import com.mehnaz.videoondemandapp.R

import com.mehnaz.videoondemandapp.databinding.FragmentMovieListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!


    private val viewModel: MovieListViewModel by viewModels()
    private lateinit var listingAdapter: MovieListPagingAdapter
    private lateinit var networkStatusLiveData: NetworkStatusLiveData

    // LoadStateAdapter for footer (used to show loading/error states at the end of the list)
    private lateinit var footerLoadStateAdapter: MovieLoadStateAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate layout using View Binding
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize main PagingData adapter, handling click to navigate to details fragment
        listingAdapter = MovieListPagingAdapter { movie ->
            val bundle = Bundle().apply {
                putString("imdbID", movie.imdbID)
            }
            findNavController().navigate(R.id.detailsFragment, bundle)
        }

        // Initialize footer LoadStateAdapter for retry & loading UI at the bottom of list
        footerLoadStateAdapter = MovieLoadStateAdapter { listingAdapter.retry() }

        // Combine main adapter and footer adapter into one ConcatAdapter
        val concatAdapter = ConcatAdapter(listingAdapter, footerLoadStateAdapter)

        // Setup grid layout manager with 2 columns
        val spanCount = 2
        val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)

        // Control how many columns each item spans (footer spans all columns)
        val footerCount = footerLoadStateAdapter.itemCount // usually 0 or 1
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                // Footer at the end spans full width
                val totalItemCount = listingAdapter.itemCount + footerCount
                if (footerCount > 0 && position == totalItemCount - 1) {
                    return spanCount
                }
                // Normal items span 1 column each
                return 1
            }
        }

        // Set layout manager and adapter on RecyclerView
        binding.recyclerListing.layoutManager = gridLayoutManager
        binding.recyclerListing.adapter = concatAdapter

        // Observe PagingData LoadState changes to update UI (progress bar, error messages)
        viewLifecycleOwner.lifecycleScope.launch {
            listingAdapter.loadStateFlow.collectLatest { loadStates ->
                footerLoadStateAdapter.loadState = loadStates.append

                // Show full screen progress bar only during initial loading
                binding.progressBar.isVisible = loadStates.refresh is LoadState.Loading
                binding.recyclerListing.isVisible = loadStates.refresh is LoadState.NotLoading

                if (loadStates.refresh is LoadState.Error) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${(loadStates.refresh as LoadState.Error).error.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                // Refresh spans on layout changes
                gridLayoutManager.requestLayout()
            }
        }

        // Observe network connectivity changes to load data or show message
        networkStatusLiveData = NetworkStatusLiveData(requireContext())
        networkStatusLiveData.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                loadMovies("Batman")
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // Handle toolbar back navigation
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    // Function to load movies with optional year filter
    private fun loadMovies(query: String, year: String? = null) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getMovies(query, year).collectLatest {
                listingAdapter.submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

