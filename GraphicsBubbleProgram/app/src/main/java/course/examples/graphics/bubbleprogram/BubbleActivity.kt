package course.examples.graphics.bubbleprogram

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout

class BubbleActivity : Activity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val relativeLayout = findViewById<RelativeLayout>(R.id.frame)

        val bubbleView = ImageView(applicationContext)

        val tmp = getDrawable(R.drawable.b512) as BitmapDrawable?
        if (null != tmp) {
            tmp.setTint(Color.WHITE)
            bubbleView.setImageDrawable(tmp)
        }

        val width = resources.getDimension(R.dimen.image_width).toInt()
        val height = resources.getDimension(R.dimen.image_height).toInt()

        val params = RelativeLayout.LayoutParams(
            width, height
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        bubbleView.layoutParams = params

        relativeLayout.addView(bubbleView)
    }
}