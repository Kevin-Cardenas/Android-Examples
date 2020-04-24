package course.examples.networking.url

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentActivity

class NetworkingURLActivity : FragmentActivity(), RetainedFragment.OnFragmentInteractionListener {

    companion object {
        private const val MTEXTVIEW_TEXT_KEY = "MTEXTVIEW_TEXT_KEY"
    }

    private lateinit var mTextView: TextView
    private lateinit var mRetainedFragment: RetainedFragment

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)
        mTextView = findViewById(R.id.textView)

        if (null != savedInstanceState) {
            mRetainedFragment = supportFragmentManager
                .findFragmentByTag(RetainedFragment.TAG) as RetainedFragment
            mTextView.text = savedInstanceState.getCharSequence(MTEXTVIEW_TEXT_KEY)
        } else {

            mRetainedFragment = RetainedFragment()
            supportFragmentManager.beginTransaction()
                .add(mRetainedFragment, RetainedFragment.TAG)
                .commit()
        }
    }

    fun onClick(v: View) {
        mRetainedFragment.onButtonPressed()
    }

    override fun onDownloadfinished(result: String) {
        mTextView.text = result
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(MTEXTVIEW_TEXT_KEY, mTextView.text)
    }
}