package course.examples.graphics.tweenanimation

import android.app.Activity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

class GraphicsTweenAnimationActivity : Activity() {

    private lateinit var mImageView: ImageView
    private lateinit var mAnim: Animation

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        mImageView = findViewById(R.id.icon)

        mAnim = AnimationUtils.loadAnimation(this, R.anim.view_animation)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            mImageView.startAnimation(mAnim)
        }
    }
}