package uz.murodjon_sattorov.simplemusicplayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import uz.murodjon_sattorov.simplemusicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private var changeRepeat: Boolean = false
    private var changePlayAndPause: Boolean = false
    private var player: MediaPlayer? = null
    private var runnable: Runnable? = null
    private var handler: Handler? = null
    private var allSongs = IntArray(10)
    private var allTitles = arrayOfNulls<String>(10)
    private var countMusic: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(
                    Manifest.permission.RECORD_AUDIO
                )
                requestPermissions(permission, 1000)
            } else {
                onStart()
            }
        } else {
            onStart()
        }

        mainBinding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    player!!.seekTo(progress)
                    mainBinding.seekbar.progress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

    }

    override fun onStart() {
        super.onStart()
        mainBinding.repeatAndShuffle.setBackgroundResource(R.drawable.ic_baseline_repeat_24)
        mainBinding.playAndPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)

        allSoundsAndTitles()
        handler = Handler()

        changeRepeatAndShuffle()
        skipPrevious()
        playAndPause()
        skipNext()
        stopBtn()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onStart()
                } else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun allSoundsAndTitles() {
        allSongs = intArrayOf(
            R.raw.song,
            R.raw.song2,
            R.raw.song3,
            R.raw.song4,
            R.raw.song5
        )
        allTitles = arrayOf(
            "Shohijahon Jo'rayev - yoring go'zal xur bo'lsin",
            "Dildora Niyozova - Onaginam",
            "Bahodir Mamajonov - qaydan bilsin",
            "Xurshid Rasulov - Bahorim",
            "Morgenshteyn - problema")
    }

    private fun changeRepeatAndShuffle() {
        mainBinding.repeatAndShuffle.setOnClickListener {
            changeRepeat = if (changeRepeat) {
                mainBinding.repeatAndShuffle.setBackgroundResource(R.drawable.ic_baseline_repeat_24)
                allSoundsAndTitles()
                false
            } else {
                mainBinding.repeatAndShuffle.setBackgroundResource(R.drawable.ic_baseline_shuffle_24)
                allSongs.shuffle()
                true
            }
        }
    }

    private fun skipPrevious() {
        mainBinding.previousBtn.setOnClickListener {
            if (player != null && countMusic != 0) {
                player?.stop()
                countMusic--
                player = MediaPlayer.create(this, allSongs[countMusic])
                player?.start()
                mainBinding.titleMusic.text = allTitles[countMusic]
                player!!.setOnCompletionListener {
                    autoNext()
                }
            } else if (countMusic == 0) {
                player?.stop()
                countMusic = allSongs.size - 1
                player = MediaPlayer.create(this, allSongs[countMusic])
                val audioSessionId = player!!.audioSessionId
                if (audioSessionId != -1) {
                    mainBinding.blob.setAudioSessionId(audioSessionId)
                }
                player?.start()
                mainBinding.titleMusic.text = allTitles[countMusic]
                player!!.setOnCompletionListener {
                    autoNext()
                }
            }

        }
    }

    private fun playAndPause() {
        mainBinding.playAndPause.setOnClickListener {

//            if (player!!.isPlaying) mainBinding.playAndPause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
//            else mainBinding.playAndPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)

            changePlayAndPause = if (changePlayAndPause) {
                mainBinding.playAndPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
                if (player != null) player?.pause()
                false
            } else {
                mainBinding.playAndPause.setBackgroundResource(R.drawable.ic_baseline_pause_24)
                if (player == null) {
                    player = MediaPlayer.create(this, allSongs[countMusic])
                    mainBinding.titleMusic.text = allTitles[countMusic]
                    val audioSessionId = player!!.audioSessionId
                    if (audioSessionId != -1) {
                        mainBinding.blob.setAudioSessionId(audioSessionId)
                    }
                    player!!.setOnCompletionListener {
                        Log.d("TAG", "playAndPause: $player")
                        autoNext()
                    }
                    player!!.setOnPreparedListener {
                        mainBinding.seekbar.max = it.duration
                        updateSeekBar()

                        mainBinding.currentDuration.text = createTimerLabel(it.duration)

                        //set max duration
                        val totTime = createTimerLabel(player!!.duration)
                        mainBinding.totalDuration.text = totTime

                    }
                }
                player?.start()
                true
            }
        }
    }

    private fun skipNext() {
        mainBinding.nextBtn.setOnClickListener {
            if (player != null && countMusic != allSongs.size - 1) {
                player?.stop()
                countMusic++
                player = MediaPlayer.create(this, allSongs[countMusic])
                val audioSessionId = player!!.audioSessionId
                if (audioSessionId != -1) {
                    mainBinding.blob.setAudioSessionId(audioSessionId)
                }
                player?.start()
                mainBinding.titleMusic.text = allTitles[countMusic]
                player!!.setOnCompletionListener {
                    autoNext()
                }
            } else if (countMusic == allSongs.size - 1) {
                player?.stop()
                countMusic = 0
                player = MediaPlayer.create(this, allSongs[countMusic])
                val audioSessionId = player!!.audioSessionId
                if (audioSessionId != -1) {
                    mainBinding.blob.setAudioSessionId(audioSessionId)
                }
                player?.start()
                mainBinding.titleMusic.text = allTitles[countMusic]
                player!!.setOnCompletionListener {
                    autoNext()
                }
            }

        }
    }

    private fun autoNext() {
        if (player != null) {
            countMusic++
            Log.d("TAG", "autoNext: $countMusic")
            player = MediaPlayer.create(this, allSongs[countMusic])
            val audioSessionId = player!!.audioSessionId
            if (audioSessionId != -1) {
                mainBinding.blob.setAudioSessionId(audioSessionId)
            }
            player?.start()
            mainBinding.titleMusic.text = allTitles[countMusic]
            player!!.setOnCompletionListener {
                if (countMusic == allSongs.size - 1) {
                    countMusic = -1
                    autoNext()
                } else autoNext()
            }
        }
    }

    private fun updateSeekBar() {
        val currentPosition = player!!.currentPosition
        mainBinding.seekbar.progress = currentPosition

        runnable = Runnable {
            updateSeekBar()
        }

        handler?.postDelayed(runnable!!, 1000)

    }

    private fun createTimerLabel(duration: Int): String {
        var timerLabel: String = ""
        val min = duration / 1000 / 60
        val sec = duration / 1000 % 60

        timerLabel += "$min:"

        if (sec < 10) timerLabel += "0"
        timerLabel += sec

        return timerLabel
    }

    private fun stopBtn() {
        mainBinding.stopBtn.setOnClickListener {
            stopPlayer()
        }
    }

    private fun stopPlayer() {
        if (player != null) {
            mainBinding.playAndPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)
            changePlayAndPause = false
            player?.release()
            player = null
            mainBinding.blob.release()
            Toast.makeText(this, "Player stop", Toast.LENGTH_SHORT).show()
        }
    }

}