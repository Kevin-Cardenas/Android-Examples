package course.examples.graphics.canvasbubble

import java.util.Random

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import kotlin.math.max

class BubbleActivity : Activity() {

    companion object {
        private const val TAG = "BubbleActivity"
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val frame = findViewById<RelativeLayout>(R.id.frame)
        val bitmap = BitmapFactory.decodeResource(
            resources,
            R.drawable.b512
        )
        val bubbleView = BubbleView(
            applicationContext,
            bitmap
        )

        frame.addView(bubbleView)

        Thread(Runnable {
            while (bubbleView.moveWhileOnscreen()) {
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    Log.i(TAG, "InterruptedException")
                }

                bubbleView.postInvalidate()
            }
        }).start()
    }

    private inner class BubbleView internal constructor(context: Context, bitmap: Bitmap) :
        View(context) {

        private val margin = 20
        private val stepSize = 100
        private val mBitmap: Bitmap
        private var mCurrentCoords: Coords
        private val mDxDy: Coords
        private val mDisplayMetrics: DisplayMetrics = DisplayMetrics()
        private val mDisplayWidth: Int
        private val mDisplayHeight: Int
        private val mBitmapWidthAndHeightAdj: Int
        private val mPainter = Paint()
        private val mBitmapWidthAndHeight: Int = resources.getDimension(
            R.dimen.image_height
        ).toInt()

        init {

            // Scale bitmap
            this.mBitmap = Bitmap.createScaledBitmap(
                bitmap,
                mBitmapWidthAndHeight, mBitmapWidthAndHeight, false
            )
            mBitmapWidthAndHeightAdj = mBitmapWidthAndHeight + margin

            // Get display size info
            this@BubbleActivity.windowManager.defaultDisplay
                .getMetrics(mDisplayMetrics)
            mDisplayWidth = mDisplayMetrics.widthPixels
            mDisplayHeight = mDisplayMetrics.heightPixels

            // Set random starting point
            val r = Random()
            val x = r.nextInt(mDisplayWidth - mBitmapWidthAndHeight).toFloat()
            val y = r.nextInt(mDisplayHeight - mBitmapWidthAndHeight).toFloat()

            mCurrentCoords = Coords(x, y)

            // Set random movement direction and speed
            var dy = max(r.nextFloat(), 0.1f) * stepSize
            dy *= (if (r.nextInt(2) == 1) 1 else -1).toFloat()
            var dx = max(r.nextFloat(), 0.1f) * stepSize
            dx *= (if (r.nextInt(2) == 1) 1 else -1).toFloat()
            mDxDy = Coords(dx, dy)

            // Add some painting directives
            mPainter.isAntiAlias = true
            mPainter.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

        }

        override fun onDraw(canvas: Canvas) {
            val tmp = mCurrentCoords.coords
            canvas.drawBitmap(mBitmap, tmp.mX, tmp.mY, mPainter)
        }

        internal fun moveWhileOnscreen(): Boolean {
            mCurrentCoords = mCurrentCoords.move(mDxDy)

            return !(mCurrentCoords.mY < 0 - mBitmapWidthAndHeightAdj
                    || mCurrentCoords.mY > mDisplayHeight + mBitmapWidthAndHeightAdj
                    || mCurrentCoords.mX < 0 - mBitmapWidthAndHeightAdj
                    || mCurrentCoords.mX > mDisplayWidth + mBitmapWidthAndHeightAdj)
        }
    }
}