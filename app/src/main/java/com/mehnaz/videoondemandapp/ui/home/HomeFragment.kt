package com.mehnaz.videoondemandapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mehnaz.videoondemandapp.R


import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.repository.MovieRepository
import com.mehnaz.videoondemandapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel

    private lateinit var batmanAdapter: HomeMovieAdapter
    private lateinit var latestAdapter: HomeMovieAdapter

    private val apiKey = "f42caa44"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(MovieRepository(apiKey)) as T
            }
        })[HomeViewModel::class.java]

        batmanAdapter = HomeMovieAdapter { movie -> navigateToDetails(movie) }
        latestAdapter = HomeMovieAdapter { movie -> navigateToDetails(movie) }

        binding.recyclerBatman.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = batmanAdapter
        }
        binding.recyclerLatest.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = latestAdapter
        }

        viewModel.batmanMovies.observe(viewLifecycleOwner) {
            batmanAdapter.submitList(it)
        }
        viewModel.latestMovies.observe(viewLifecycleOwner) {
            latestAdapter.submitList(it)
        }
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        }

        viewModel.fetchHomeData()
    }

    private fun navigateToDetails(movie: MovieItem) {
       // val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(movie.imdbID)
       // findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
