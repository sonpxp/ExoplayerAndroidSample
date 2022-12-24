package com.cloudxanh.exoplayerandroidsample

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import com.cloudxanh.exoplayerandroidsample.databinding.ActivityMainBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.util.MimeTypes


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var videoPlayer: ExoPlayer? = null
    private var isFullScreen = false

    private var sampleUrl =
        "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"
    private val uri2 = "https://tinyurl.com/2qdnfe9z"
    private val uri3 =
        "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializePlayer()
        exoFullScreen()
    }

    private fun initializePlayer() {
        videoPlayer = ExoPlayer.Builder(this)
            .setSeekBackIncrementMs(10000) // Back 10s
            .setSeekForwardIncrementMs(10000) // Forward 10s
            .build()

        // create a media item.
        val mediaItem = MediaItem.Builder()
            .setUri(uri3)
            .setMimeType(MimeTypes.APPLICATION_MP4)
            .build()

        // Create a media source and pass the media item
        val mediaSource = ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(this)
        ).createMediaSource(mediaItem)

        if (videoPlayer == null) return
        // Finally assign this media source to the player
        videoPlayer!!.apply {
            setMediaSource(mediaSource)
            playWhenReady = true // start playing when the exoplayer has setup
            seekTo(0, 0L) // Start from the beginning
            prepare() // Change the state from idle.
        }.also {
            // Do not forget to attach the player to the view
            binding.videoPlayerView.player = it
            binding.videoPlayerView.keepScreenOn = true
        }
    }

    private fun handleView() {
        videoPlayer!!.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
            }
        })
    }

    private fun exoFullScreen() {
        val exoPlayerFullScreen = findViewById<ImageButton>(R.id.exo_full_screen)
        exoPlayerFullScreen.setOnClickListener {
            if (!isFullScreen) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                dimensionRatio()
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            isFullScreen = !isFullScreen
        }
    }

    private fun dimensionRatio() {
        // fix ti le 16:9 cho layout custom khi xoay ngang
        val mLayout = binding.root
        val set = ConstraintSet()
        set.clone(mLayout)
        set.setDimensionRatio(binding.videoPlayerView.id, "0")
        set.applyTo(mLayout)
    }

    override fun onResume() {
        super.onResume()
        videoPlayer?.playWhenReady = true
    }

    override fun onStop() {
        super.onStop()
        videoPlayer?.playWhenReady = false
        if (isFinishing) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        videoPlayer?.release()
    }
}