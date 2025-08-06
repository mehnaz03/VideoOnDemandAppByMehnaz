package com.mehnaz.videoondemandapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.mehnaz.videoondemandapp.R


import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mehnaz.videoondemandapp.data.model.MovieItem
import com.mehnaz.videoondemandapp.data.repository.MovieRepository
import com.mehnaz.videoondemandapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!



    private lateinit var batmanAdapter: HomeMovieAdapter
    private lateinit var latestAdapter: HomeMovieAdapter
    private val viewModel: HomeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



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
        val bundle = Bundle().apply {
            putString("imdbID", movie.imdbID)
        }
        findNavController().navigate(R.id.detailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
