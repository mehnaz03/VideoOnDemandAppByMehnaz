package com.mehnaz.videoondemandapp.ui.home

import android.os.Bundle
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
import com.mehnaz.videoondemandapp.utils.NetworkStatusLiveData

import com.mehnaz.videoondemandapp.data.model.MovieItem

import com.mehnaz.videoondemandapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint

class HomeFragment : Fragment() {

    // View binding for accessing layout views
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Adapter for banner slider (ViewPager2)
    private lateinit var bannerAdapter: BannerAdapter

    // Coroutine job for auto-scrolling banners
    private var autoScrollJob: Job? = null

    // Adapters for horizontal movie lists
    private lateinit var batmanAdapter: HomeMovieAdapter
    private lateinit var latestAdapter: HomeMovieAdapter

    // ViewModel for fetching home screen data
    private val viewModel: HomeViewModel by viewModels()

    // LiveData to monitor network status in real time
    private lateinit var networkStatusLiveData: NetworkStatusLiveData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout and initialize binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerViews and adapters
        setupRecyclerViews()

        // Setup banner slider with auto-scroll and indicators
        setupBannerSlider()

        // Observe ViewModel data and bind to UI
        observeViewModel()

        // Initialize network status observer
        networkStatusLiveData = NetworkStatusLiveData(requireContext())

        // Observe network changes and react accordingly
        networkStatusLiveData.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                // Fetch data when network is available
                viewModel.fetchHomeData()
            } else {
                // Show toast when offline
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerViews() {
        // Initialize adapters with click listeners
        batmanAdapter = HomeMovieAdapter { movie -> navigateToDetails(movie) }
        latestAdapter = HomeMovieAdapter { movie -> navigateToDetails(movie) }

        // Setup horizontal scrolling RecyclerViews
        binding.recyclerBatman.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = batmanAdapter
        }

        binding.recyclerLatest.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = latestAdapter
        }

        // Show shimmer loading until data is loaded
        batmanAdapter.showShimmerLoading(true)
        latestAdapter.showShimmerLoading(true)
    }

    private fun setupBannerSlider() {
        // Initialize banner adapter with click listener
        bannerAdapter = BannerAdapter { movie ->
            navigateToDetails(movie)
            Toast.makeText(requireContext(), "Clicked: ${movie.Title}", Toast.LENGTH_SHORT).show()
        }

        binding.viewPagerBanner.apply {
            adapter = bannerAdapter
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 1

            // Add margin between pages
            val pageMargin = resources.getDimensionPixelOffset(R.dimen.banner_page_margin)
            val pageTransformer = MarginPageTransformer(pageMargin)
            setPageTransformer(pageTransformer)

            // Disable overscroll effect
            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            // Update indicator when page changes
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateIndicator(position)
                }
            })
        }
    }

    private fun observeViewModel() {
        // Observe banner movies and update slider
        viewModel.bannerMovies.observe(viewLifecycleOwner) { banners ->
            bannerAdapter.submitList(banners)
            setupIndicators(banners.size)
            startAutoScroll()
        }

        // Observe Batman movie list
        viewModel.batmanMovies.observe(viewLifecycleOwner) {
            batmanAdapter.submitList(it)
            batmanAdapter.showShimmerLoading(false)
        }

        // Observe latest movie list
        viewModel.latestMovies.observe(viewLifecycleOwner) {
            latestAdapter.submitList(it)
            latestAdapter.showShimmerLoading(false)
        }

        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        }

        // See All click listeners (navigate to list page)
        binding.tvBatmanSeeAll.setOnClickListener {
            findNavController().navigate(R.id.movielistFragment)
           // Toast.makeText(requireContext(), "See all Batman clicked", Toast.LENGTH_SHORT).show()
        }

        binding.tvLatestSeeAll.setOnClickListener {
            findNavController().navigate(R.id.movielistFragment)
           // Toast.makeText(requireContext(), "See all Latest clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupIndicators(count: Int) {
        // Clear existing indicators
        binding.indicatorLayout.removeAllViews()

        // Create and add new indicators
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
        // Highlight the active indicator
        val count = binding.indicatorLayout.childCount
        for (i in 0 until count) {
            val imageView = binding.indicatorLayout.getChildAt(i) as ImageView
            imageView.setImageResource(
                if (i == position) R.drawable.indicaator_active else R.drawable.indicator_inactive
            )
        }
    }

    private fun startAutoScroll() {
        // Start auto-scrolling banner every 4 seconds
        autoScrollJob?.cancel()
        autoScrollJob = viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                delay(4000)
                val itemCount = bannerAdapter.itemCount
                if (itemCount > 0) {
                    val nextItem = (binding.viewPagerBanner.currentItem + 1) % itemCount
                    binding.viewPagerBanner.setCurrentItem(nextItem, true)
                }
            }
        }
    }

    private fun stopAutoScroll() {
        // Cancel auto-scroll when fragment is destroyed
        autoScrollJob?.cancel()
        autoScrollJob = null
    }

    private fun navigateToDetails(movie: MovieItem) {
        // Navigate to details fragment with IMDb ID
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




