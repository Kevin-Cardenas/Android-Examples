package course.examples.threading.threadingrunonuithread

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SimpleThreadingExample : Activity() {
    private lateinit var mImageView: ImageView
    private val mDelay = 5000L

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        mImageView = findViewById(R.id.imageView)

    }

    fun onClickOtherButton(v: View) {
        Toast.makeText(
            this@SimpleThreadingExample, "I'm Working",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun onClickLoadButton(view: View) {
        mImageView.isEnabled = false
        GlobalScope.launch (Dispatchers.IO) {
            delay(mDelay)

            this@SimpleThreadingExample.runOnUiThread {
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