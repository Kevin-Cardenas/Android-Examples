package course.examples.threading.threadingviewpost

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SimpleThreadingViewPostActivity : Activity() {
    private lateinit var mImageView: ImageView
    private val mDelay = 5000L

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        mImageView = findViewById(R.id.imageView)
    }

    fun onClickOtherButton(v: View) {
        Toast.makeText(
            this@SimpleThreadingViewPostActivity, "I'm Working",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun onClickLoadButton(view: View) {
        view.isEnabled = false
        GlobalScope.launch (Dispatchers.IO) {
            delay(mDelay)

            mImageView.post {
                mImageView.setImageBitmap(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.painter
                    )
                )
            }
        }
    }
}