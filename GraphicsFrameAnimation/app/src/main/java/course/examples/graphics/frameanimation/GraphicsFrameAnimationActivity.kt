package course.examples.graphics.frameanimation

import android.app.Activity
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.ImageView

class GraphicsFrameAnimationActivity : Activity() {
    private lateinit var mAnim: AnimationDrawable

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val imageView = findViewById<ImageView>(R.id.countdown_frame)
        imageView.setBackgroundResource(R.drawable.view_animation)
        mAnim = imageView.background as AnimationDrawable
    }


    override fun onPause() {
        super.onPause()
        if (mAnim.isRunning) {
            mAnim.stop()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            mAnim.start()
        }
    }
}