package course.examples.graphics.canvasbubblesurfaceview

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
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.RelativeLayout

import java.util.Random
import kotlin.math.max

class BubbleActivity : Activity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        val relativeLayout = findViewById<RelativeLayout>(R.id.frame)
        val bubbleView = BubbleView(
            applicationContext,
            BitmapFactory.decodeResource(resources, R.drawable.b512)
        )

        relativeLayout.addView(bubbleView)
    }

    private inner class BubbleView internal constructor(context: Context, bitmap: Bitmap) :
        SurfaceView(context), SurfaceHolder.Callback {

        private val mBitmap: Bitmap
        private val mBitmapHeightAndWidth: Int = resources.getDimension(
            R.dimen.image_height_width
        ).toInt()
        private val mBitmapWidthAndHeightAdj: Int
        private val mBitmapHeightAndWidthPadded: Float
        private val mBitmapPadding = 80.0f

        private val mDisplay: DisplayMetrics
        private val mDisplayWidth: Int
        private val mDisplayHeight: Int

        private var mRotation: Float = 0.0f

        private val mSurfaceHolder: SurfaceHolder
        private var mDrawingThread: Thread? = null
        private val mPainter = Paint()

        private val mBackgroundColor: Int =
            context.resources.getColor(R.color.background_color, null)
        private val mDxDy: Coords
        private var mCurrent: Coords? = null
        private val mStep = 40
        private val mRotStep = 1.0f


        init {

            // Resize bubble
            this.mBitmap = Bitmap.createScaledBitmap(
                bitmap,
                mBitmapHeightAndWidth, mBitmapHeightAndWidth, false
            )

            // Set useful parameters
            mBitmapWidthAndHeightAdj = mBitmapHeightAndWidth / 2
            mBitmapHeightAndWidthPadded = mBitmapHeightAndWidth + mBitmapPadding

            // Determine screen size
            mDisplay = DisplayMetrics()
            this@BubbleActivity.windowManager.defaultDisplay
                .getMetrics(mDisplay)
            mDisplayWidth = mDisplay.widthPixels
            mDisplayHeight = mDisplay.heightPixels

            // Set random starting point for BubbleView
            val r = Random()
            val x = r.nextInt(mDisplayWidth / 4).toFloat() + mDisplayWidth / 4
            val y = r.nextInt(mDisplayHeight / 4).toFloat() + mDisplayHeight / 4
            mCurrent = Coords(x, y)

            // Set random movement direction and speed, and set rotation
            var dy = max(r.nextFloat(), 0.1f) * mStep
            dy *= (if (r.nextInt(2) == 1) 1 else -1).toFloat()
            var dx = max(r.nextFloat(), 0.1f) * mStep
            dx *= (if (r.nextInt(2) == 1) 1 else -1).toFloat()
            mDxDy = Coords(dx, dy)
            mRotation = 1.0f

            mPainter.isAntiAlias = true
            mPainter.colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

            // Prepare surface for drawing
            mSurfaceHolder = holder
            mSurfaceHolder.addCallback(this)
        }

        private fun drawBubble(canvas: Canvas) {
            canvas.drawColor(mBackgroundColor)
            mRotation += mRotStep
            canvas.rotate(
                mRotation,
                mCurrent!!.mX + mBitmapWidthAndHeightAdj,
                mCurrent!!.mY + mBitmapWidthAndHeightAdj
            )
            canvas.drawBitmap(mBitmap, mCurrent!!.mX, mCurrent!!.mY, mPainter)
        }

        private fun moveWhileOnscreen(): Boolean {
            mCurrent = mCurrent!!.move(mDxDy)

            return !(mCurrent!!.mY < 0 - mBitmapHeightAndWidthPadded
                    || mCurrent!!.mY > mDisplayHeight + mBitmapHeightAndWidthPadded
                    || mCurrent!!.mX < 0 - mBitmapHeightAndWidthPadded
                    || mCurrent!!.mX > mDisplayWidth + mBitmapHeightAndWidthPadded)
        }

        override fun surfaceChanged(
            holder: SurfaceHolder, format: Int, width: Int,
            height: Int
        ) {
            // Not implemented
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            mDrawingThread = Thread(Runnable {
                while (!Thread.currentThread().isInterrupted && moveWhileOnscreen()) {
                    val canvas = mSurfaceHolder.lockCanvas()
                    if (null != canvas) {
                        drawBubble(canvas)
                        mSurfaceHolder.unlockCanvasAndPost(canvas)
                    }
                }
            })
            mDrawingThread?.start()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            if (null != mDrawingThread)
                mDrawingThread!!.interrupt()
        }
    }
}