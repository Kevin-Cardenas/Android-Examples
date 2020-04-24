package course.examples.graphics.viewpropertyanimator

import android.app.Activity
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView

class GraphicsViewPropertyAnimatorActivity : Activity() {

    private lateinit var mImageView: ImageView

    private val fadeIn = Runnable {
        mImageView.animate().setDuration(3000)
            .setInterpolator(LinearInterpolator()).alpha(1.0f)
            .withEndAction(rotate)
    }

    private val rotate = Runnable {
        mImageView.animate().setDuration(4000)
            .setInterpolator(AccelerateInterpolator())
            .rotationBy(720.0f).withEndAction(translate)
    }

    private val translate = Runnable {
        val translation = resources
            .getDimension(R.dimen.translation)
        mImageView.animate().setDuration(3000)
            .setInterpolator(OvershootInterpolator())
            .translationXBy(translation).translationYBy(translation)
            .withEndAction(scale)
    }

    private val scale = Runnable {
        mImageView.animate().setDuration(3000)
            .setInterpolator(AnticipateInterpolator())
            .scaleXBy(1.0f).scaleYBy(1.0f).withEndAction(fadeOut)
    }

    private val fadeOut = Runnable {
        mImageView.animate().setDuration(2000)
            .setInterpolator(DecelerateInterpolator()).alpha(0.0f)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        mImageView = findViewById(R.id.bubble)

        if (hasFocus) {
            fadeIn.run()
        }
    }

}