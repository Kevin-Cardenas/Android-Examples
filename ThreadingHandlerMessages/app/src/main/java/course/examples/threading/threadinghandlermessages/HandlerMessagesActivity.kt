package course.examples.threading.threadinghandlermessages

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast

class HandlerMessagesActivity : Activity() {

    companion object {
        internal const val SET_PROGRESS_BAR_VISIBILITY = 0
        internal const val PROGRESS_UPDATE = 1
        internal const val SET_BITMAP = 2
        private const val PROG_BAR_PROGRESS_KEY = "PROG_BAR_PROGRESS_KEY"
        private const val PROG_BAR_VISIBLE_KEY = "PROG_BAR_VISIBLE_KEY"
        private const val BITMAP_KEY = "BITMAP_KEY"
    }

    private lateinit var mImageView: ImageView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mLoadIconTask: LoadIconTask

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
            mLoadIconTask.setmProgressBar(mProgressBar)
                .setmImageView(mImageView)
        }
    }

    fun onClickLoadButton(v: View) {
        mLoadIconTask = LoadIconTask(applicationContext)
            .setmImageView(mImageView)
            .setmProgressBar(mProgressBar)
        mLoadIconTask.start()
    }

    fun onClickOtherButton(v: View) {
        Toast.makeText(
            this@HandlerMessagesActivity, "I'm Working",
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
