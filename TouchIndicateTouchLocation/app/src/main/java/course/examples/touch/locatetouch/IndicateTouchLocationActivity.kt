package course.examples.touch.locatetouch

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.FrameLayout

import java.util.HashMap
import java.util.LinkedList
import java.util.Random
import kotlin.math.abs

class IndicateTouchLocationActivity : Activity() {

    companion object {

        private const val MAX_SIZE = 400f
        private const val MIN_DXDY = 2

        // Assume no more than 20 simultaneous touches
        private const val MAX_TOUCHES = 20

        // Pool of MarkerViews
        private val mInactiveMarkers = LinkedList<MarkerView>()

        // Set of MarkerViews currently visible on the display
        @SuppressLint("UseSparseArrays")
        private val mActiveMarkers = HashMap<Int, MarkerView>()

        private const val TAG = "IndicateTouchLoc"
    }

    private lateinit var mFrame: FrameLayout

    @SuppressLint("ClickableViewAccessibility")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        mFrame = findViewById(android.R.id.content)

        // Initialize pool of View.
        initViews()

        // Create and set on touch listener
        mFrame.setOnTouchListener(object : OnTouchListener {

            override fun onTouch(v: View, event: MotionEvent): Boolean {

                v.performClick()

                when (event.actionMasked) {

                    // Show new MarkerView

                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {

                        val pointerIndex = event.actionIndex
                        val pointerID = event.getPointerId(pointerIndex)

                        val marker = mInactiveMarkers.remove()

                        marker?.apply {
                            mActiveMarkers[pointerID] = marker
                            xLoc = event.getX(pointerIndex)
                            yLoc = event.getY(pointerIndex)
                            updateTouches(mActiveMarkers.size)
                            mFrame.addView(marker)
                        }
                    }

                    // Remove one MarkerView

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {

                        val pointerIndex = event.actionIndex
                        val pointerID = event.getPointerId(pointerIndex)

                        val marker = mActiveMarkers.remove(pointerID)

                        marker?.apply {
                            mInactiveMarkers.add(this)
                            updateTouches(mActiveMarkers.size)
                            mFrame.removeView(marker)
                        }
                    }

                    // Move all currently active MarkerViews
                    MotionEvent.ACTION_MOVE -> {

                        for (idx in 0 until event.pointerCount) {

                            val currId = event.getPointerId(idx)

                            val marker = mActiveMarkers[currId]
                            marker?.apply {

                                // Redraw only if finger has traveled a minimum distance
                                if (abs(xLoc - event.getX(idx)) > MIN_DXDY || abs(
                                        yLoc - event.getY(idx)
                                    ) > MIN_DXDY
                                ) {

                                    // Set new location
                                    xLoc = event.getX(idx)
                                    yLoc = event.getY(idx)

                                    // Request re-draw
                                    invalidate()
                                }
                            }
                        }
                    }

                    else -> Log.i(TAG, "unhandled action")
                }

                return true
            }

            // update number of touches on each active MarkerView
            private fun updateTouches(numActive: Int) {
                for (marker in mActiveMarkers.values) {
                    marker.setTouches(numActive)
                }
            }
        })
    }

    private fun initViews() {
        for (idx in 0 until MAX_TOUCHES) {
            mInactiveMarkers.add(MarkerView(this, -1f, -1f))
        }
    }

    private inner class MarkerView internal constructor(
        context: Context,
        internal var xLoc: Float,
        internal var yLoc: Float
    ) : View(context) {
        private var mTouches = 0
        private val mPaint = Paint()

        init {

            mPaint.style = Style.FILL

            val rnd = Random()
            mPaint.setARGB(
                255, rnd.nextInt(256), rnd.nextInt(256),
                rnd.nextInt(256)
            )
        }

        internal fun setTouches(touches: Int) {
            mTouches = touches
        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawCircle(xLoc, yLoc, (MAX_SIZE / mTouches), mPaint)
        }
    }
}