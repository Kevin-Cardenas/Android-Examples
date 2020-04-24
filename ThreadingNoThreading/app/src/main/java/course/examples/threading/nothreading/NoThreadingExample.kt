package course.examples.threading.nothreading

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast

class NoThreadingExample : Activity() {
    private lateinit var mIView: ImageView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        mIView = findViewById(R.id.imageView)
    }

    fun onClickOtherButton(v: View) {
        Toast.makeText(
            this@NoThreadingExample, "I'm Working",
            Toast.LENGTH_SHORT
        ).show()
    }


    fun onClickLoadButton(view: View) {
        try {
            // Accentuates pretend slow operation
            Thread.sleep(5000)
            mIView.setImageBitmap(BitmapFactory.decodeResource(resources, R.drawable.painter))
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}