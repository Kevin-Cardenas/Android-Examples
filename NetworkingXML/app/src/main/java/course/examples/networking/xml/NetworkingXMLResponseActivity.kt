package course.examples.networking.xml

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.FragmentActivity

class NetworkingXMLResponseActivity : FragmentActivity(),
    RetainedFragment.OnFragmentInteractionListener {

    private lateinit var mRetainedFragment: RetainedFragment
    private lateinit var mListView: ListView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_xml_response)
        mListView = findViewById(R.id.list)

        if (null != savedInstanceState) {
            mRetainedFragment = supportFragmentManager
                .findFragmentByTag(RetainedFragment.TAG) as RetainedFragment
            onDownloadfinished()
        } else {

            mRetainedFragment = RetainedFragment()
            supportFragmentManager.beginTransaction()
                .add(mRetainedFragment, RetainedFragment.TAG)
                .commit()
            mRetainedFragment.onButtonPressed()
        }
    }


    override fun onDownloadfinished() {
        mRetainedFragment.data?.let {
            mListView.adapter = ArrayAdapter(
                this@NetworkingXMLResponseActivity,
                R.layout.list_item, it
            )
        }
    }
}