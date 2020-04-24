package course.examples.graphics.shapedraw

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout

class ShapeDrawActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val width = resources.getDimension(R.dimen.image_width).toInt()
        val height = resources.getDimension(R.dimen.image_height).toInt()
        val padding = resources.getDimension(R.dimen.padding).toInt()

        // Get container View
        val rl = findViewById<RelativeLayout>(R.id.main_window)

        // Create Cyan Shape
        val cyanShape = ShapeDrawable(OvalShape())
        cyanShape.paint.color = Color.CYAN
        cyanShape.intrinsicHeight = height
        cyanShape.intrinsicWidth = width
        cyanShape.alpha = ALPHA

        // Put Cyan Shape into an ImageView
        val cyanView = ImageView(applicationContext)
        cyanView.setImageDrawable(cyanShape)
        cyanView.setPadding(padding, padding, padding, padding)

        // Specify placement of ImageView within RelativeLayout
        val cyanViewLayoutParams = RelativeLayout.LayoutParams(
            height, width
        )
        cyanViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        cyanViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        cyanView.layoutParams = cyanViewLayoutParams
        rl.addView(cyanView)

        // Create Magenta Shape
        val magentaShape = ShapeDrawable(OvalShape())
        magentaShape.paint.color = Color.MAGENTA
        magentaShape.intrinsicHeight = height
        magentaShape.intrinsicWidth = width
        magentaShape.alpha = ALPHA

        // Put Magenta Shape into an ImageView
        val magentaView = ImageView(applicationContext)
        magentaView.setImageDrawable(magentaShape)
        magentaView.setPadding(padding, padding, padding, padding)

        // Specify placement of ImageView within RelativeLayout
        val magentaViewLayoutParams = RelativeLayout.LayoutParams(
            height, width
        )
        magentaViewLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        magentaViewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)

        magentaView.layoutParams = magentaViewLayoutParams

        rl.addView(magentaView)

    }

    companion object {
        private const val ALPHA = 127
    }
}