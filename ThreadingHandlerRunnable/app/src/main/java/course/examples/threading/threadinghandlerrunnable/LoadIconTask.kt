package course.examples.threading.threadinghandlerrunnable

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.widget.ImageView
import android.widget.ProgressBar

class LoadIconTask internal constructor(private val mAppContext: Context) : Thread() {
    private val mHandler: Handler = Handler()
    private val mBitmapResID = R.drawable.painter

    private var mProgressBar: ProgressBar? = null
    private var mImageView: ImageView? = null

    internal fun setImageView(mImageView: ImageView): LoadIconTask {
        this.mImageView = mImageView
        return this
    }

    internal fun setProgressBar(mProgressBar: ProgressBar): LoadIconTask {
        this.mProgressBar = mProgressBar
        return this
    }

    override fun run() {
        if (null == mProgressBar || null == mImageView) return
        
        mHandler.post { mProgressBar!!.visibility = ProgressBar.VISIBLE }

        // Simulating long-running operation

        for (i in 1..10) {
            sleep()
            mHandler.post { mProgressBar!!.progress = i * 10 }
        }

        mHandler.post {
            mImageView!!.setImageBitmap(
                BitmapFactory.decodeResource(mAppContext.resources, mBitmapResID)
            )
        }

        mHandler.post { mProgressBar!!.visibility = ProgressBar.INVISIBLE }
    }

    private fun sleep() = try {
        val mDelay = 500L
        sleep(mDelay)
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }
}
