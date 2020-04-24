package course.examples.threading.threadinghandlermessages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Message
import android.widget.ImageView
import android.widget.ProgressBar

class LoadIconTask internal constructor(private val mContext: Context) : Thread() {

    private val mHandler: UIHandler

    init {
        mHandler = UIHandler()
    }

    override fun run() {

        var msg = mHandler.obtainMessage(
            HandlerMessagesActivity.SET_PROGRESS_BAR_VISIBILITY,
            ProgressBar.VISIBLE
        )
        mHandler.sendMessage(msg)

        val mResId = R.drawable.painter
        val tmp = BitmapFactory.decodeResource(mContext.resources, mResId)

        for (i in 1..10) {
            sleep()
            msg = mHandler.obtainMessage(HandlerMessagesActivity.PROGRESS_UPDATE, i * 10)
            mHandler.sendMessage(msg)
        }

        msg = mHandler.obtainMessage(HandlerMessagesActivity.SET_BITMAP, tmp)
        mHandler.sendMessage(msg)

        msg = mHandler.obtainMessage(
            HandlerMessagesActivity.SET_PROGRESS_BAR_VISIBILITY,
            ProgressBar.INVISIBLE
        )
        mHandler.sendMessage(msg)
    }

    fun setmImageView(mImageView: ImageView): LoadIconTask {
        mHandler.setmImageView(mImageView)
        return this
    }

    fun setmProgressBar(mProgressBar: ProgressBar): LoadIconTask {
        mHandler.setmProgressBar(mProgressBar)
        return this
    }

    private fun sleep() {
        try {
            val mDelay = 500L
            sleep(mDelay)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    private class UIHandler : Handler() {

        private var mImageView: ImageView? = null
        private var mProgressBar: ProgressBar? = null

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                HandlerMessagesActivity.SET_PROGRESS_BAR_VISIBILITY -> {
                    mProgressBar!!.visibility = msg.obj as Int
                }
                HandlerMessagesActivity.PROGRESS_UPDATE -> {
                    mProgressBar!!.progress = msg.obj as Int
                }
                HandlerMessagesActivity.SET_BITMAP -> {
                    mImageView!!.setImageBitmap(msg.obj as Bitmap)
                }
            }
        }

        fun setmImageView(mImageView: ImageView) {
            this.mImageView = mImageView
        }

        fun setmProgressBar(mProgressBar: ProgressBar) {
            this.mProgressBar = mProgressBar
        }
    }
}