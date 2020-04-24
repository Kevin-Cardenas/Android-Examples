package course.examples.audiovideo.videoplay

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView

class AudioVideoVideoPlayActivity : Activity() {
    private var mVideoView: VideoView? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        // Get a reference to the VideoView

        mVideoView = findViewById(R.id.videoViewer)

        // Add a Media controller to allow forward/reverse/pause/resume
        val mMediaController = MediaController(this, true)
        mMediaController.isEnabled = false
        mVideoView?.setMediaController(mMediaController)

        mVideoView?.setVideoURI(
            Uri.parse("android.resource://" + packageName + "/" + R.raw.moon)
        )

        // Add an OnPreparedListener to enable the MediaController once the video is ready
        mVideoView?.setOnPreparedListener { mMediaController.isEnabled = true }
    }

    // Clean up and release resources
    override fun onPause() {

        mVideoView?.apply {
            if (isPlaying) stopPlayback()
            mVideoView = null
        }
        super.onPause()
    }
}