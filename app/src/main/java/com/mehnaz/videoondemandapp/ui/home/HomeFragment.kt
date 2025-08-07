package com.mehnaz.videoondemandapp.ui.home

import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast

import androidx.fragment.app.viewModels
import com.mehnaz.videoondemandapp.R

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2

import com.mehnaz.videoondemandapp.data.model.MovieItem

import com.mehnaz.videoondemandapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.logging.Handler

@AndroidEntryPoint

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var bannerAdapter: BannerAdapter
    private var autoScrollJob: Job? = null

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

        setupRecyclerViews()
        setupBannerSlider()
        observeViewModel()

        viewModel.fetchHomeData()
    }

    private fun setupRecyclerViews() {
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

        batmanAdapter.showShimmerLoading(true)
        latestAdapter.showShimmerLoading(true)
    }

    private fun setupBannerSlider() {
        bannerAdapter = BannerAdapter { movie ->
            Toast.makeText(requireContext(), "Clicked: ${movie.Title}", Toast.LENGTH_SHORT).show()
        }

        binding.viewPagerBanner.apply {
            adapter = bannerAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 1

            val pageMargin = resources.getDimensionPixelOffset(R.dimen.banner_page_margin) // 8dp
            val pageTransformer = MarginPageTransformer(pageMargin)
            setPageTransformer(pageTransformer)

            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateIndicator(position)
                }
            })
        }
    }

    private fun observeViewModel() {
        viewModel.bannerMovies.observe(viewLifecycleOwner) { banners ->
            bannerAdapter.submitList(banners)
            setupIndicators(banners.size)
            startAutoScroll()
        }

        viewModel.batmanMovies.observe(viewLifecycleOwner) {
            batmanAdapter.submitList(it)
            batmanAdapter.showShimmerLoading(false)
        }

        viewModel.latestMovies.observe(viewLifecycleOwner) {
            latestAdapter.submitList(it)
            latestAdapter.showShimmerLoading(false)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupIndicators(count: Int) {
        binding.indicatorLayout.removeAllViews()
        val indicators = Array(count) {
            ImageView(requireContext()).apply {
                setImageResource(R.drawable.indicator_inactive)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(4, 0, 4, 0)
                }
                layoutParams = params
            }
        }

        indicators.forEach { binding.indicatorLayout.addView(it) }
        updateIndicator(0)
    }

    private fun updateIndicator(position: Int) {
        val count = binding.indicatorLayout.childCount
        for (i in 0 until count) {
            val imageView = binding.indicatorLayout.getChildAt(i) as ImageView
            imageView.setImageResource(
                if (i == position) R.drawable.indicaator_active else R.drawable.indicator_inactive
            )
        }
    }

    private fun startAutoScroll() {
        autoScrollJob?.cancel()
        autoScrollJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                delay(3000)
                val itemCount = bannerAdapter.itemCount
                if (itemCount > 0) {
                    val nextItem = (binding.viewPagerBanner.currentItem + 1) % itemCount
                    binding.viewPagerBanner.setCurrentItem(nextItem, true)
                }
            }
        }
    }

    private fun stopAutoScroll() {
        autoScrollJob?.cancel()
        autoScrollJob = null
    }

    private fun navigateToDetails(movie: MovieItem) {
        val bundle = Bundle().apply {
            putString("imdbID", movie.imdbID)
        }
        findNavController().navigate(R.id.detailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopAutoScroll()
        _binding = null
    }
}



