package course.examples.graphics.transitiondrawable

import android.app.Activity
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.widget.ImageView

class TransitionDrawableActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)

        val transition = resources
            .getDrawable(R.drawable.shape_transition, null) as TransitionDrawable

        transition.isCrossFadeEnabled = true

        (findViewById<ImageView>(R.id.image_view)).setImageDrawable(transition)

        transition.startTransition(5000)
    }
}