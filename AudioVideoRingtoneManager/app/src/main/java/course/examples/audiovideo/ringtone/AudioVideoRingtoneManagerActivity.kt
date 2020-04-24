package course.examples.audiovideo.ringtone

import android.app.Activity
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.os.Handler
import android.view.View

class AudioVideoRingtoneManagerActivity : Activity() {
    private var mCurrentRingtone: Ringtone? = null
    private lateinit var mHandler: Handler

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        mHandler = Handler()

    }

    fun onClickRingtoneButton(v: View) {
        playRingtone(RingtoneManager.TYPE_RINGTONE)
    }

    fun onClickAlarmButton(v: View) {
        playRingtone(RingtoneManager.TYPE_ALARM)
    }

    fun onClickNotifButton(v: View) {
        playRingtone(RingtoneManager.TYPE_NOTIFICATION)
    }

    // Shut off current Ringtone and play new one
    private fun playRingtone(newRingtoneType: Int) {

        mCurrentRingtone?.apply {
            if (isPlaying)
                stop()
        }

        mCurrentRingtone = RingtoneManager.getRingtone(
            applicationContext, RingtoneManager
                .getDefaultUri(newRingtoneType)
        )

        mCurrentRingtone?.apply {
            play()
            // Stop any ringtones playing in 5 seconds
            mHandler.postDelayed({ stop() }, 5000)
        }
    }

    // Stop ringtones playing when Activity pauses
    override fun onPause() {
        mCurrentRingtone?.apply {
            if (isPlaying)
                stop()
            super.onPause()
        }
    }
}