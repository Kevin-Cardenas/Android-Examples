package course.examples.threading.threadinghandlerrunnable

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast

class HandlerRunnableActivity : Activity() {

    companion object {
        private const val PROG_BAR_PROGRESS_KEY = "PROG_BAR_PROGRESS_KEY"
        private const val PROG_BAR_VISIBLE_KEY = "PROG_BAR_VISIBLE_KEY"
        private const val BITMAP_KEY = "BITMAP_KEY"
    }

    private lateinit var mLoadIconTask: LoadIconTask
    private lateinit var mImageView: ImageView
    private lateinit var mProgressBar: ProgressBar

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        mImageView = findViewById(R.id.imageView)
        mProgressBar = findViewById(R.id.progressBar)

        savedInstanceState?.let {
            mProgressBar.visibility = it.getInt(PROG_BAR_VISIBLE_KEY)
            mProgressBar.progress = it.getInt(PROG_BAR_PROGRESS_KEY)
            mImageView.setImageBitmap(it.getParcelable(BITMAP_KEY))
            mLoadIconTask = lastNonConfigurationInstance as LoadIconTask
            mLoadIconTask.setProgressBar(mProgressBar)
                .setImageView(mImageView)
        }
    }

    fun onClickLoadButton(v: View) {
        v.isEnabled = false
        mLoadIconTask = LoadIconTask(applicationContext)
            .setImageView(mImageView)
            .setProgressBar(mProgressBar)
        mLoadIconTask.start()
    }

    fun onClickOtherButton(v: View) {
        Toast.makeText(
            this@HandlerRunnableActivity, "I'm Working",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PROG_BAR_VISIBLE_KEY, mProgressBar.visibility)
        outState.putInt(PROG_BAR_PROGRESS_KEY, mProgressBar.progress)

        if (null != mImageView.drawable) {
            val bm = (mImageView.drawable as BitmapDrawable).bitmap
            outState.putParcelable(BITMAP_KEY, bm)
        }
    }

    override fun onRetainNonConfigurationInstance(): Any? {
        return mLoadIconTask
    }
}
