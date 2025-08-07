package com.mehnaz.videoondemandapp.ui.movielist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listingAdapter = MovieListPagingAdapter { movie ->
            val bundle = Bundle().apply {
                putString("imdbID", movie.imdbID)
            }
            findNavController().navigate(R.id.detailsFragment, bundle)
        }

        binding.recyclerListing.apply {
            layoutManager = LinearLayoutManager(requireContext())

            binding.recyclerListing.adapter = listingAdapter.withLoadStateHeaderAndFooter(
                header = MovieLoadStateAdapter { listingAdapter.retry() },
                footer = MovieLoadStateAdapter { listingAdapter.retry() }
            )

        }

        loadMovies("Batman")

        // ✅ Safe access to binding using viewLifecycleOwner
        viewLifecycleOwner.lifecycleScope.launch {
            listingAdapter.loadStateFlow.collectLatest { loadState ->
                _binding?.let { binding ->
//                    binding.progressBar.visibility = if (loadState.refresh is LoadState.Loading) {
//                        View.VISIBLE
//                    } else {
//                        View.GONE
//                    }

                    val errorState = when {
                        loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                        loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                        loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                        else -> null
                    }

                    errorState?.let {
                        Toast.makeText(requireContext(), "Error: ${it.error.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

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