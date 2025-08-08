package com.mehnaz.videoondemandapp.ui.details

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import com.mehnaz.videoondemandapp.utils.NetworkStatusLiveData
import com.mehnaz.videoondemandapp.databinding.FragmentDetailsBinding
import com.mehnaz.videoondemandapp.ui.home.HomeMovieAdapter
import com.mehnaz.videoondemandapp.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    // ViewBinding variable (nullable)
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    // ExoPlayer instance for video playback
    private var player: ExoPlayer? = null
    private var playbackPosition: Long = 0L

    // Keys for saving playback position in SharedPreferences
    private val playbackPositionKey = "playback_position"
    private val playbackPositionPref = "playback_pref"

    // Holds the current movie's IMDb ID
    private var movieImdbId: String? = null

    // Sample video URL for playback (replace with real video URL)
    private val sampleVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"

    // ViewModels: DetailsViewModel for movie details, HomeViewModel for related movies
    private val viewModel: DetailsViewModel by viewModels()
    private val homeviewModel: HomeViewModel by viewModels()

    // Adapters for "More Like This" and "Related" movie lists
    private lateinit var batmanAdapter: HomeMovieAdapter
    private lateinit var latestAdapter: HomeMovieAdapter

    // LiveData to observe network connectivity
    private lateinit var networkStatusLiveData: NetworkStatusLiveData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate layout using ViewBinding
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get IMDb ID passed via navigation arguments
        movieImdbId = arguments?.getString("imdbID")
        movieImdbId?.let { viewModel.loadMovieDetails(it) }

        // Observe movie details LiveData and update UI
        viewModel.movieDetail.observe(viewLifecycleOwner) { movie ->
            binding.tvTitle.text = movie.Title
            binding.tvGenre.text = movie.Genre ?: ""
            binding.tvPlot.text = movie.Plot ?: ""
        }

        // Initialize adapters with click listeners for toast message
        batmanAdapter = HomeMovieAdapter { movie ->
            Toast.makeText(requireContext(), "Clicked: ${movie.Title}", Toast.LENGTH_SHORT).show()
        }
        latestAdapter = HomeMovieAdapter { movie ->
            Toast.makeText(requireContext(), "Clicked: ${movie.Title}", Toast.LENGTH_SHORT).show()
        }

        // Setup RecyclerViews for related lists
        setupRecyclerViews()

        // Observe batmanMovies list and submit data to adapter, hide shimmer loading
        homeviewModel.batmanMovies.observe(viewLifecycleOwner) {
            batmanAdapter.submitList(it)
            batmanAdapter.showShimmerLoading(false)
        }

        // Observe latestMovies list and submit data to adapter, hide shimmer loading
        homeviewModel.latestMovies.observe(viewLifecycleOwner) {
            latestAdapter.submitList(it)
            latestAdapter.showShimmerLoading(false)
        }

        // Fetch home data (batman and latest movie lists)
        homeviewModel.fetchHomeData()

        // Back button click listener to navigate back
        binding.backIcon.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Observe error messages from ViewModel and show toast
        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        }

        // Observe network connectivity changes
        networkStatusLiveData = NetworkStatusLiveData(requireContext())
        networkStatusLiveData.observe(viewLifecycleOwner) { isConnected ->
            if (isConnected) {
                // If player is null, initialize it; otherwise resume playback
                if (player == null) {
                    initializePlayer()
                } else {
                    player?.play()
                }
                // Reload bottom lists data when network is restored
                homeviewModel.fetchHomeData()
                // Reload movie details if IMDb ID exists
                movieImdbId?.let { viewModel.loadMovieDetails(it) }
            } else {
                Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
                // Pause player if network is lost
                player?.pause()
            }
        }
    }

    // Setup RecyclerViews for "More Like This" and "Related" movie lists
    private fun setupRecyclerViews() {
        binding.rvMoreLikeThis.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = batmanAdapter
        }

        binding.rvRelated.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = latestAdapter
        }

        // Show shimmer effect loading initially
        batmanAdapter.showShimmerLoading(true)
        latestAdapter.showShimmerLoading(true)
    }

    // Initialize ExoPlayer and start playback from saved position if available
    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(sampleVideoUrl))
        player?.setMediaItem(mediaItem)

        // Restore playback position if available
        playbackPosition = getPlaybackPosition()
        player?.seekTo(playbackPosition)
        player?.prepare()
        player?.play()

        // Add listeners for playback state and errors
        player?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                // Loop video when playback ends
                if (playbackState == Player.STATE_ENDED) {
                    player?.seekTo(0L)
                    player?.play()
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                // Save playback position when playback pauses
                if (!isPlaying) {
                    savePlaybackPosition(player?.currentPosition ?: 0)
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("PlayerError", "Playback error: ${error.message}", error)
                Toast.makeText(requireContext(), "Playback error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Save playback position to SharedPreferences
    private fun savePlaybackPosition(position: Long) {
        val prefs = requireContext().getSharedPreferences(playbackPositionPref, Context.MODE_PRIVATE)
        prefs.edit().putLong("$playbackPositionKey-$movieImdbId", position).apply()
    }

    // Retrieve saved playback position from SharedPreferences
    private fun getPlaybackPosition(): Long {
        val prefs = requireContext().getSharedPreferences(playbackPositionPref, Context.MODE_PRIVATE)
        return prefs.getLong("$playbackPositionKey-$movieImdbId", 0L)
    }

    override fun onPause() {
        super.onPause()
        // Save playback position and pause player on pause
        player?.let {
            savePlaybackPosition(it.currentPosition)
            it.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        // Release player resources on stop
        player?.release()
        player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Release player resources on view destroy
        player?.release()
        player = null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release player resources on fragment destroy
        player?.release()
        player = null
    }
}


