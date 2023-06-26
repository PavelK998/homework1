package ru.pakarp.homework1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper

import android.util.Log
import android.widget.SeekBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import ru.pakarp.homework1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ServiceConnection {
    private lateinit var binding: ActivityMainBinding
    private lateinit var runnable: Runnable
    private var handler = Handler(Looper.getMainLooper())
    var mediaService: PlayerService? = null
    private val currentSong =
        mutableListOf(R.raw.infinity, R.raw.sweater_weather, R.raw.walk, R.raw.wellerman)
    private var ifNeedsToResume = false
    var chosenSong = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val intent = Intent(this, PlayerService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
        startService(intent)

        binding.fbPause.setOnClickListener {
            if (mediaService?.mp == null) {
                createMP()
            } else if (mediaService?.mp?.isPlaying == false) {
                startPlaying()
            } else pause()
            initializeSeekBar()
        }

        binding.fbNext.setOnClickListener {
            nextTrack()
        }

        binding.fbPrevious.setOnClickListener {
            previousTrack()
        }
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val localBinder = service as PlayerService.LocalBinder

        mediaService = localBinder.getBindService()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        mediaService = null
    }

    private fun createMP() {
        try {
            if (mediaService!!.mp == null) {
                mediaService!!.mp = MediaPlayer()
                mediaService!!.mp?.reset()
                mediaService!!.mp?.setDataSource(resources.openRawResourceFd(currentSong[chosenSong]))
                mediaService!!.mp?.prepare()
                mediaService!!.mp?.start()
                binding.fbPause.setImageResource(R.drawable.pause)
            }
        } catch (e: Exception) {
            return
        }
    }

    private fun pause() {
        mediaService!!.mp?.pause()
        binding.fbPause.setImageResource(R.drawable.play_arrow)
    }

    private fun startPlaying() {
        mediaService!!.mp?.start()
        binding.fbPause.setImageResource(R.drawable.pause)
    }

    private fun nextTrack() {
        chosenSong++
        if (chosenSong > currentSong.size - 1) chosenSong = 0
        mediaService!!.mp?.reset()
        mediaService!!.mp?.setDataSource(resources.openRawResourceFd(currentSong[chosenSong]))
        mediaService!!.mp?.prepare()
        mediaService!!.mp?.start()
        binding.fbPause.setImageResource(R.drawable.pause)
    }

    private fun previousTrack() {
        chosenSong--
        if (chosenSong < 0) chosenSong = currentSong.size - 1
        mediaService!!.mp?.reset()
        mediaService!!.mp?.setDataSource(resources.openRawResourceFd(currentSong[chosenSong]))
        mediaService!!.mp?.prepare()
        mediaService!!.mp?.start()
        binding.fbPause.setImageResource(R.drawable.pause)
    }

    private fun initializeSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaService!!.mp?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                ifNeedsToResume = if (mediaService?.mp?.isPlaying == true) {
                    mediaService?.mp?.pause()
                    true
                } else false
            }


            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (ifNeedsToResume) {
                    mediaService?.mp?.start()
                }
            }
        })
        binding.seekBar.max = mediaService?.mp?.duration!!
        runnable = Runnable {
            binding.seekBar.progress = mediaService?.mp?.currentPosition!!
            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }
}













