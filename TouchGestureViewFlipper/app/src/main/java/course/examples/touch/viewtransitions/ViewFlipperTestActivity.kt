package course.examples.touch.viewtransitions

import android.app.Activity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView
import android.widget.ViewFlipper

class ViewFlipperTestActivity : Activity() {
    private lateinit var mFlipper: ViewFlipper
    private lateinit var mTextView1: TextView
    private lateinit var mTextView2: TextView
    private var mCurrentLayoutState: Int = 0
    private var mCount: Int = 0
    private lateinit var mGestureDetector: GestureDetector

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        mCurrentLayoutState = 0

        mFlipper = findViewById(R.id.view_flipper)
        mTextView1 = findViewById(R.id.textView1)
        mTextView2 = findViewById(R.id.textView2)

        mTextView1.text = mCount.toString()

        mGestureDetector = GestureDetector(this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent, e2: MotionEvent,
                    velocityX: Float, velocityY: Float
                ): Boolean {
                    if (velocityX < -10.0f) {
                        mCurrentLayoutState = if (mCurrentLayoutState == 0)
                            1 else 0
                        switchLayoutStateTo(mCurrentLayoutState)
                    }
                    return true
                }
            })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    private fun switchLayoutStateTo(switchTo: Int) {
        mCurrentLayoutState = switchTo

        mFlipper.inAnimation = inFromRightAnimation()
        mFlipper.outAnimation = outToLeftAnimation()

        mCount++

        if (switchTo == 0) {
            mTextView1.text = mCount.toString()
        } else {
            mTextView2.text = mCount.toString()
        }

        mFlipper.showPrevious()
    }

    private fun inFromRightAnimation(): Animation {
        val inFromRight = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, +1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromRight.duration = 500
        inFromRight.interpolator = LinearInterpolator()
        return inFromRight
    }

    private fun outToLeftAnimation(): Animation {
        val outToLeft = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        outToLeft.duration = 500
        outToLeft.interpolator = LinearInterpolator()
        return outToLeft
    }
}