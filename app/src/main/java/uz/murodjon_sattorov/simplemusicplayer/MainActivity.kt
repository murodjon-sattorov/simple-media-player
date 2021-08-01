package uz.murodjon_sattorov.simplemusicplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer
import uz.murodjon_sattorov.simplemusicplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private var changeRepeat: Boolean = false
    private var changePlayAndPause: Boolean = false
    private var player: MediaPlayer? = null
    private var allSongs = IntArray(10)
    private var countMusic: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.repeatAndShuffle.setBackgroundResource(R.drawable.ic_baseline_repeat_24)
        mainBinding.playAndPause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24)

        allSounds()

        changeRepeatAndShuffle()
        skipPrevious()
        playAndPause()
        skipNext()
        stopBtn()

    }
    private fun allSounds(){
        allSongs = intArrayOf(
            R.raw.sound1,
            R.raw.sound2,
            R.raw.sound3,
            R.raw.sound4
        )
    }

    private fun changeRepeatAndShuffle() {
        mainBinding.repeatAndShuffle.setOnClickListener {
            changeRepeat = if (changeRepeat) {
                mainBinding.repeatAndShuffle.setBackgroundResource(R.drawable.ic_baseline_repeat_24)
                allSounds()
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
                player!!.setOnCompletionListener {
                    autoNext()
                }
            }else if (countMusic == 0) {
                player?.stop()
                countMusic = allSongs.size - 1
                player = MediaPlayer.create(this, allSongs[countMusic])
                player?.start()
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
                    player!!.setOnCompletionListener {
                        Log.d("TAG", "playAndPause: $player")
                        autoNext()
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
                player?.start()
                player!!.setOnCompletionListener {
                    autoNext()
                }
            }else if (countMusic == allSongs.size - 1) {
                player?.stop()
                countMusic = 0
                player = MediaPlayer.create(this, allSongs[countMusic])
                player?.start()
                player!!.setOnCompletionListener {
                    autoNext()
                }
            }

        }
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
            Toast.makeText(this, "Player stop", Toast.LENGTH_SHORT).show()
        }
    }

    private fun autoNext() {
        if (player != null) {
            countMusic++
            Log.d("TAG", "autoNext: $countMusic")
            player = MediaPlayer.create(this, allSongs[countMusic])
            player?.start()
            player!!.setOnCompletionListener {
                if (countMusic == allSongs.size - 1) {
                    countMusic = -1
                    autoNext()
                } else autoNext()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopPlayer()
    }
}