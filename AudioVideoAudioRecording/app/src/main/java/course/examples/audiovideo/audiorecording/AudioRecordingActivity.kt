package course.examples.audiovideo.audiorecording

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.media.*
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import android.widget.ToggleButton
import java.io.File
import java.io.IOException


// These audio functions won't survive rotation. Should prevent rotation, especially during recording

class AudioRecordingActivity : Activity(), OnAudioFocusChangeListener {

    companion object {
        private const val TAG = "AudioRecordTest"
        private const val APP_PERMS_REQUEST_CODE = 200
    }

    private lateinit var mFileName: String
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private lateinit var mAudioManager: AudioManager


    private val mPermissions =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private lateinit var mRecordButton: ToggleButton
    private lateinit var mPlayButton: ToggleButton
    private lateinit var mPlaybackAttributes: AudioAttributes
    private lateinit var mFocusRequest: AudioFocusRequest
    private val mFocusLock = Any()
    private var mPlaybackDelayed: Boolean = false
    private var mResumeOnFocusGain: Boolean = false


    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.main)

        mFileName = application.getExternalFilesDir(null)?.absolutePath + "/audiorecordtest.3gp"
        mRecordButton = findViewById(R.id.record_button)
        mPlayButton = findViewById(R.id.play_button)

        if (checkSelfPermission(mPermissions[0]) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                mPermissions[1]
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(mPermissions, APP_PERMS_REQUEST_CODE)
        } else {
            continueApp()
        }

        // Set up record Button
        mRecordButton.setOnCheckedChangeListener { _, isChecked ->
            // Set enabled state
            mPlayButton.isEnabled = !isChecked

            // Start/stop recording
            onRecordPressed(isChecked)
        }

        // Set up play Button
        mPlayButton.setOnCheckedChangeListener { _, isChecked ->
            // Set enabled state
            mRecordButton.isEnabled = !isChecked

            // Start/stop playback
            onPlayPressed(isChecked)
        }

        // Get AudioManager
        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Set up AudioFocusRequest
        mPlaybackAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        mFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(mPlaybackAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setWillPauseWhenDucked(true)
            .setOnAudioFocusChangeListener(this, Handler(Looper.getMainLooper()))
            .build()

    }

    // Toggle recording
    private fun onRecordPressed(shouldStartRecording: Boolean) {

        if (shouldStartRecording) {
            startRecording()
        } else {
            stopRecording()
        }
    }

    // Start recording with MediaRecorder
    private fun startRecording() {

        mRecorder = MediaRecorder()
        mRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(mFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(TAG, "Couldn't prepare and start MediaRecorder")
            }
        }


    }

    // Stop recording. Release resources
    private fun stopRecording() {

        mRecorder?.apply {
            stop()
            release()
            mRecorder = null
        }
    }

    // Toggle playback
    private fun onPlayPressed(shouldStartPlaying: Boolean) {

        if (shouldStartPlaying) {
            startPlaying()
        } else {
            stopPlaying()
        }
    }

    // Playback audio using MediaPlayer
    private fun startPlaying() {

        mPlayer = MediaPlayer()
        mPlayer?.apply {
            setAudioAttributes(mPlaybackAttributes)
            setOnCompletionListener {
                mPlayButton.performClick()
                mPlayButton.isChecked = false

                try {
                    if (File(mFileName).exists()) {
                        apply {
                            setDataSource(mFileName)
                            prepare()
                            requestAudioFocus()
                        }
                    } else {
                        Toast.makeText(
                            this@AudioRecordingActivity,
                            R.string.no_recording_made,
                            Toast.LENGTH_LONG
                        ).show()
                        mPlayButton.isChecked = false
                    }

                } catch (e: IOException) {
                    Log.e(TAG, "Couldn't prepare and start MediaPlayer")
                }
                mAudioManager.abandonAudioFocusRequest(mFocusRequest)
            }
        }
    }

    private fun requestAudioFocus() {
        // requesting audio focus
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
        mPlayer?.start()
    }

    // Stop playback. Release resources
    private fun stopPlaying() {
        mPlayer?.apply {
            if (isPlaying) {
                stop()
                mAudioManager.abandonAudioFocusRequest(mFocusRequest)
            }
            release()
            mPlayer = null
        }
    }

    // Pause playback
    private fun pausePlaying() {
        mPlayer?.apply {
            if (isPlaying) {
                pause()
            }
        }
    }

    // implementation of the OnAudioFocusChangeListener
    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> if (mPlaybackDelayed || mResumeOnFocusGain) {
                synchronized(mFocusLock) {
                    mPlaybackDelayed = false
                    mResumeOnFocusGain = false
                }
                startPlayback()
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                synchronized(mFocusLock) {
                    // this is not a transient loss, we shouldn't automatically resume for now
                    mResumeOnFocusGain = false
                    mPlaybackDelayed = false
                }
                pausePlaying()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // we handle all transient losses the same way because we never duck audio books
                synchronized(mFocusLock) {
                    // we should only resume if playback was interrupted
                    mResumeOnFocusGain = mPlayer!!.isPlaying
                    mPlaybackDelayed = false
                }
                pausePlaying()
            }
        }
    }

    // Release recording and playback resources, if necessary
    public override fun onPause() {
        super.onPause()

        mRecorder?.apply {
            release()
            mRecorder = null
        }

        mPlayer?.apply {
            release()
            mPlayer = null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (APP_PERMS_REQUEST_CODE == requestCode) {
            var permsFailed = false
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        applicationContext,
                        R.string.need_perms_string,
                        Toast.LENGTH_LONG
                    ).show()
                    permsFailed = true
                    break
                }
            }
            if (!permsFailed) {
                continueApp()
            }
        }
    }

    private fun continueApp() {
        mPlayButton.isEnabled = true
        mRecordButton.isEnabled = true
    }
}