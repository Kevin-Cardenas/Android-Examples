package course.examples.touch.gestures

import android.app.Activity
import android.gesture.Gesture
import android.gesture.GestureLibraries
import android.gesture.GestureLibrary
import android.gesture.GestureOverlayView
import android.gesture.GestureOverlayView.OnGesturePerformedListener
import android.graphics.Color
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import java.util.*

class GesturesActivity : Activity(), OnGesturePerformedListener {

    companion object {
        private const val NO = "No"
        private const val YES = "Yes"
        private const val PREV = "Prev"
        private const val NEXT = "Next"
    }

    private lateinit var mLibrary: GestureLibrary
    private var mBgColor = 0
    private var mFirstColor: Int = 0
    private val mStartBgColor = Color.GRAY
    private lateinit var mFrame: FrameLayout
    private lateinit var mLayout: RelativeLayout

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        mFrame = findViewById(R.id.frame)
        mBgColor = Random().nextInt(0xFFFFFF) or -0x1000000
        mFirstColor = mBgColor
        mFrame.setBackgroundColor(mBgColor)

        mLayout = findViewById(R.id.main)
        mLayout.setBackgroundColor(mStartBgColor)

        // Add gestures file - contains 4 gestures: Prev, Next, Yes, No
        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures)
        if (!mLibrary.load()) {
            finish()
        }

        // Make this the target of gesture detection callbacks
        val gestureView = findViewById<GestureOverlayView>(R.id.gestures_overlay)
        gestureView.addOnGesturePerformedListener(this)

    }

    override fun onGesturePerformed(overlay: GestureOverlayView, gesture: Gesture) {

        // Get gesture predictions
        val predictions = mLibrary.recognize(gesture)

        // Get highest-ranked prediction
        if (predictions.size > 0) {
            val prediction = predictions[0]

            // Ignore weak predictions

            if (prediction.score > 2.0) {
                when (prediction.name) {
                    PREV -> {
                        mBgColor -= 100
                        mFrame.setBackgroundColor(mBgColor)
                    }
                    NEXT -> {
                        mBgColor += 100
                        mFrame.setBackgroundColor(mBgColor)
                    }
                    YES ->
                        mLayout.setBackgroundColor(mBgColor)
                    NO -> {
                        mLayout.setBackgroundColor(mStartBgColor)
                        mFrame.setBackgroundColor(mFirstColor)
                    }
                }
                Toast.makeText(this, prediction.name, Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, "No prediction", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, "No prediction", Toast.LENGTH_SHORT)
                .show()
        }
    }
}
