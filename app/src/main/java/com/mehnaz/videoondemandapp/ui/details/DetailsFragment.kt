package com.mehnaz.videoondemandapp.ui.details

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.mehnaz.videoondemandapp.R
import com.mehnaz.videoondemandapp.data.repository.MovieRepository
import com.mehnaz.videoondemandapp.databinding.FragmentDetailsBinding
import com.mehnaz.videoondemandapp.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private var player: ExoPlayer? = null

    private val playbackPositionKey = "playback_position"
    private val playbackPositionPref = "playback_pref"

    private var movieImdbId: String? = null
    private var playbackPosition: Long = 0L

    private val viewModel: DetailsViewModel by viewModels()

    private val sampleVideoUrl = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movieImdbId = arguments?.getString("imdbId")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        movieImdbId?.let { viewModel.loadMovieDetails(it) }

        viewModel.movieDetail.observe(viewLifecycleOwner) { movie ->
            binding.tvTitle.text = movie.Title
            binding.tvGenre.text = movie.Genre ?: ""
            binding.tvPlot.text = movie.Plot ?: ""

//            Glide.with(this)
//                .load(movie.Poster)
//                .into(binding.ivPoster)
        }

        viewModel.error.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(Uri.parse(sampleVideoUrl))
        player?.setMediaItem(mediaItem)

        playbackPosition = getPlaybackPosition()
        player?.seekTo(playbackPosition)
        player?.prepare()
        player?.play()

        player?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying) {
                    savePlaybackPosition(player?.currentPosition ?: 0)
                }
            }
        })
    }

    private fun savePlaybackPosition(position: Long) {
        val prefs = requireContext().getSharedPreferences(playbackPositionPref, Context.MODE_PRIVATE)
        prefs.edit().putLong("$playbackPositionKey-$movieImdbId", position).apply()
    }

    private fun getPlaybackPosition(): Long {
        val prefs = requireContext().getSharedPreferences(playbackPositionPref, Context.MODE_PRIVATE)
        return prefs.getLong("$playbackPositionKey-$movieImdbId", 0L)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        player?.let {
            savePlaybackPosition(it.currentPosition)
            it.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
