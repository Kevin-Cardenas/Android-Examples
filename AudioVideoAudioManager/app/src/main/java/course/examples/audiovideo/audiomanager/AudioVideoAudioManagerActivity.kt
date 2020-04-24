package course.examples.audiovideo.audiomanager

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class AudioVideoAudioManagerActivity : Activity(), OnAudioFocusChangeListener {

    companion object {
        private const val TAG = "AudioVideoAudioManager"
    }

    private var mVolume = 6
    private val mVolumeMin = 0
    private val mVolumeMax = 10
    private var mSoundPool: SoundPool? = null
    private var mSoundId: Int = 0
    private lateinit var mAudioManager: AudioManager
    private lateinit var mTextView: TextView
    private lateinit var mPlayButton: Button
    private lateinit var mFocusRequest: AudioFocusRequest
    private val mFocusLock = Any()
    private var mPlaybackDelayed: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        // Get reference to the AudioManager
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Display current volume level in TextView
        mTextView = findViewById(R.id.textView1)
        mTextView.text = mVolume.toString()

        mPlayButton = findViewById(R.id.play_button)

        // Disable the Play Button so user can't click it before sounds are ready
        mPlayButton.isEnabled = false

        // Set up AudioFocusRequest
        val mPlaybackAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        mFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(mPlaybackAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(this, Handler(Looper.getMainLooper()))
            .build()

    }

    fun onUpButtonClick(v: View) {
        // Play key click sound
        mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK)

        if (mVolume < mVolumeMax) {
            mVolume += 2
            mTextView.text = mVolume.toString()
        }
    }


    fun onDownButtonClick(v: View) {
        // Play key click sound
        mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK)

        if (mVolume > mVolumeMin) {
            mVolume -= 2
            mTextView.text = mVolume.toString()
        }
    }

    // Play the sound using the SoundPool
    fun onPlayButtonClick(v: View) {
        requestAudioFocus()
    }

    private fun requestAudioFocus() {
        // Requesting audio focus
        val res = mAudioManager.requestAudioFocus(mFocusRequest)
        synchronized(mFocusLock) {
            when (res) {
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> mPlaybackDelayed = false
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
                    mPlaybackDelayed = false
                    startPlayback()
                }
                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> mPlaybackDelayed = true
            }
        }
    }

    private fun startPlayback() {

        mSoundPool?.play(
            mSoundId, mVolume.toFloat() / mVolumeMax,
            mVolume.toFloat() / mVolumeMax, 1, 0, 1.0f
        )
        Log.i(TAG, "Starting playback in startPlayback()")

    }

    private fun stopPlayback() {
        mSoundPool?.stop(mSoundId)
        mAudioManager.abandonAudioFocusRequest(mFocusRequest)
        Log.i(TAG, "Abandoning AudioFocus in stopPlayback()")

    }

    // Get ready to play sound effects
    override fun onResume() {
        super.onResume()

        loadSoundPool()
        mAudioManager.isSpeakerphoneOn = true
        mAudioManager.loadSoundEffects()

    }

    private fun loadSoundPool() {
        // Create a SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        mSoundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .build()

        mSoundPool?.apply {
            // Load bubble popping sound into the SoundPool
            mSoundId = load(this@AudioVideoAudioManagerActivity, R.raw.ding_harsh, 1)

            // Set an OnLoadCompleteListener on the SoundPool
            setOnLoadCompleteListener { _, _, status ->
                // If sound loading was successful enable the play Button
                if (0 == status) {
                    mPlayButton.isEnabled = true
                } else {
                    Log.i(TAG, "Unable to load sound")
                    finish()
                }
            }
        }
    }

    // Release resources & clean up
    override fun onPause() {

        mSoundPool?.apply {
            unload(mSoundId)
            release()
            mSoundPool = null
        }

        mAudioManager.isSpeakerphoneOn = false
        mAudioManager.unloadSoundEffects()

        super.onPause()
    }

    // implementation of the OnAudioFocusChangeListener
    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> if (mPlaybackDelayed) {
                synchronized(mFocusLock) {
                    mPlaybackDelayed = false
                }
                startPlayback()
            }
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                synchronized(mFocusLock) {
                    mPlaybackDelayed = false
                }
                stopPlayback()
            }
        }
    }
}