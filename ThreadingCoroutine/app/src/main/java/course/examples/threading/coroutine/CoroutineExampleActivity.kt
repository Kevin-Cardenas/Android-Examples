/*
 * When "Load Icon" Button is pressed throws 
 * android.view.ViewRootImpl$CalledFromWrongThreadException: 
 * Only the original thread that created a view hierarchy can touch its views.
 */

package course.examples.threading.coroutine

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.coroutines.*

class CoroutineExampleActivity : Activity() {

    companion object {
        private const val mDelay = 5000L
    }

    private lateinit var mIView: ImageView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        mIView = findViewById(R.id.imageView)
    }

    fun onClickLoadButton(v: View) {

        mIView.isEnabled = false

        GlobalScope.launch(Dispatchers.Main) {
            val bitmap = withContext(Dispatchers.IO) {
                // public suspend fun delay(timeMillis: Long)
                delay(mDelay)
                BitmapFactory.decodeResource(resources, R.drawable.painter)
            }
            bitmap?.apply { mIView.setImageBitmap(this) }
        }
    }

    fun onClickOtherButton(v: View) {
        Toast.makeText(
            this@CoroutineExampleActivity, "I'm Working",
            Toast.LENGTH_SHORT
        ).show()
    }
}