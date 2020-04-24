/*
 * When "Load Icon" Button is pressed throws 
 * android.view.ViewRootImpl$CalledFromWrongThreadException: 
 * Only the original thread that created a view hierarchy can touch its views.
 */

package course.examples.threading.threadingsimple

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SimpleThreadingExample : Activity() {

    companion object {

        private const val TAG = "SimpleThreadingExample"
        private const val mDelay = 5000L
    }

    private lateinit var mIView: ImageView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        mIView = findViewById(R.id.imageView)
    }

    fun onClickLoadButton(v: View) {
        GlobalScope.launch (Default){
            delay(mDelay)
            Log.i(TAG, "In onClickLoadButton")

            // This doesn't work in Android
            mIView.setImageBitmap(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.painter
                )
            )
        }
    }

    fun onClickOtherButton(v: View) {
        Toast.makeText(
            this@SimpleThreadingExample, "I'm Working",
            Toast.LENGTH_SHORT
        ).show()
    }
}