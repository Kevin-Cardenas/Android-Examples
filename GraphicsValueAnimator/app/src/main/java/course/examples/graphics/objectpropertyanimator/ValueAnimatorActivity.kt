package course.examples.graphics.objectpropertyanimator

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView

class ValueAnimatorActivity : Activity() {

    companion object {
        private const val RED = Color.RED
        private const val BLUE = Color.BLUE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
    }

    fun onClick(v: View) {
        startAnimation()
    }

    private fun startAnimation() {

        val imageView = findViewById<ImageView>(R.id.image_view)

        val anim = ValueAnimator.ofObject(
            ArgbEvaluator(), RED,
            BLUE
        )

        anim.addUpdateListener { animation ->
            imageView.setBackgroundColor(
                animation
                    .animatedValue as Int
            )
        }

        anim.duration = 10000
        anim.start()
    }
}
