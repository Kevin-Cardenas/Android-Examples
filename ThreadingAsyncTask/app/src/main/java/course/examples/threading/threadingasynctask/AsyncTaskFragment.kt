package course.examples.threading.threadingasynctask

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

import java.lang.ref.WeakReference

class AsyncTaskFragment : Fragment() {

    companion object {
        const val TAG = "AsyncTaskFragment"
        private const val PAINTER = R.drawable.painter
    }

    private var mListener: OnFragmentInteractionListener? = null
    internal var imageBitmap: Bitmap? = null
        private set(result) {
            field = result
            mListener?.setImageBitmap(result!!)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
    }

    fun onButtonPressed() {
        LoadIconTask(this).execute(PAINTER)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    // Upcalls to hosting Activity
    private fun setProgressBarVisibility(isVisible: Int) {
        mListener?.setProgressBarVisibility(isVisible)
    }

    private fun setProgress(value: Int?) {
        mListener?.setProgress(value)
    }

    // Interface to Hosting Activity
    interface OnFragmentInteractionListener {
        fun setProgressBarVisibility(isVisible: Int)
        fun setImageBitmap(result: Bitmap)
        fun setProgress(value: Int?)
    }

    private class LoadIconTask(fragment: AsyncTaskFragment) : AsyncTask<Int, Int, Bitmap>() {
        // GC can reclaim weakly referenced variables.
        private val mAsyncTaskFragment: WeakReference<AsyncTaskFragment> = WeakReference(fragment)

        override fun onPreExecute() {
            mAsyncTaskFragment.get()?.setProgressBarVisibility(ProgressBar.VISIBLE)
        }

        override fun doInBackground(vararg resId: Int?): Bitmap {
            // simulating long-running operation
            for (i in 1..10) {
                sleep()
                publishProgress(i * 10)
            }
            return BitmapFactory.decodeResource(mAsyncTaskFragment.get()?.resources, resId[0]!!)
        }

        override fun onProgressUpdate(vararg values: Int?) {
            mAsyncTaskFragment.get()?.setProgress(values[0])
        }

        override fun onPostExecute(result: Bitmap) {
            mAsyncTaskFragment.get()?.setProgressBarVisibility(ProgressBar.INVISIBLE)
            mAsyncTaskFragment.get()?.imageBitmap = result
        }

        private fun sleep() {
            try {
                val mDelay = 500L
                Thread.sleep(mDelay)
            } catch (e: InterruptedException) {
                Log.e(TAG, e.toString())
            }

        }
    }
}
