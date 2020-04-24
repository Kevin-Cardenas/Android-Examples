package course.examples.threading.threadingasynctask

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentActivity

class AsyncTaskActivity : FragmentActivity(), AsyncTaskFragment.OnFragmentInteractionListener {

    companion object {
        private const val PROG_BAR_PROGRESS_KEY = "PROG_BAR_PROGRESS_KEY"
        private const val PROG_BAR_VISIBLE_KEY = "PROG_BAR_VISIBLE_KEY"
    }

    private lateinit var mImageView: ImageView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mLoadButton: Button
    private lateinit var mAsyncTaskFragment: AsyncTaskFragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)
        mImageView = findViewById(R.id.imageView)
        mProgressBar = findViewById(R.id.progressBar)
        mLoadButton = findViewById(R.id.loadButton)

        if (null != savedInstanceState) {
            mProgressBar.visibility = savedInstanceState.getInt(PROG_BAR_VISIBLE_KEY)
            mProgressBar.progress = savedInstanceState.getInt(PROG_BAR_PROGRESS_KEY)
            mAsyncTaskFragment = supportFragmentManager
                .findFragmentByTag(AsyncTaskFragment.TAG) as AsyncTaskFragment
            mImageView.setImageBitmap(mAsyncTaskFragment.imageBitmap)
        } else {
            // Create headless Fragment that runs LoadIconAsyncTask and stores the loaded bitmap
            mAsyncTaskFragment = AsyncTaskFragment()
            supportFragmentManager
                .beginTransaction()
                .add(mAsyncTaskFragment, AsyncTaskFragment.TAG)
                .commit()
        }
    }

    fun onClickLoadButton(v: View) {
        mLoadButton.isEnabled = false
        mAsyncTaskFragment.onButtonPressed()
    }


    fun onClickOtherButton(v: View) {
        Toast.makeText(
            this@AsyncTaskActivity, "I'm Working",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(PROG_BAR_VISIBLE_KEY, mProgressBar.visibility)
        outState.putInt(PROG_BAR_PROGRESS_KEY, mProgressBar.progress)
    }


    // Callbacks from AsyncTaskFragment
    override fun setProgressBarVisibility(isVisible: Int) {
        mProgressBar.visibility = isVisible
    }

    override fun setProgress(value: Int?) {
        mProgressBar.progress = value!!
    }

    override fun setImageBitmap(result: Bitmap) {
        mImageView.setImageBitmap(result)
    }
}