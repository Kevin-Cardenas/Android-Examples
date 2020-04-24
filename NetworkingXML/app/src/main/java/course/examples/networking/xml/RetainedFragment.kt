package course.examples.networking.xml

import androidx.fragment.app.Fragment
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.ArrayList

class RetainedFragment : Fragment() {

    companion object {
        internal const val TAG = "RetainedFragment"
    }

    private var mListener: OnFragmentInteractionListener? = null
    internal var data: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    internal fun onButtonPressed() {
        HttpGetTask(this).execute()
    }

    private fun onDownloadFinished(result: List<String>) {
        data = result
        if (null != mListener) {
            mListener!!.onDownloadfinished()
        }
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

    interface OnFragmentInteractionListener {
        fun onDownloadfinished()
    }

    internal class HttpGetTask(retainedFragment: RetainedFragment) :
        AsyncTask<Void, Void, List<String>>() {

        companion object {
            private const val TAG = "HttpGetTask"
            // Get your own user name at http://www.geonames.org/login
            private const val USER_NAME = "aporter"
            private const val HOST = "api.geonames.org"
            private const val URL =
                ("http://" + HOST + "/earthquakes?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
                        + USER_NAME)
            private const val EARTHQUAKE_TAG = "earthquake"
            private const val MAGNITUDE_TAG = "magnitude"
            private const val LONGITUDE_TAG = "lng"
            private const val LATITUDE_TAG = "lat"
        }

        private val mListener: WeakReference<RetainedFragment> = WeakReference(retainedFragment)
        private var mLat: String? = null
        private var mLng: String? = null
        private var mMag: String? = null
        private var mIsParsingLat: Boolean = false
        private var mIsParsingLng: Boolean = false
        private var mIsParsingMag: Boolean = false
        private val mResults = ArrayList<String>()


        override fun doInBackground(vararg params: Void): List<String>? {
            var data: String? = null
            var httpUrlConnection: HttpURLConnection? = null

            try {
                // 1. Get connection. 2. Prepare request (URI)
                httpUrlConnection = URL(URL)
                    .openConnection() as HttpURLConnection

                // 3. This app does not use a request body
                // 4. Read the response
                val inputStream = BufferedInputStream(
                    httpUrlConnection.inputStream
                )

                data = readStream(inputStream)
            } catch (exception: MalformedURLException) {
                Log.e(TAG, "MalformedURLException")
            } catch (exception: IOException) {
                Log.e(TAG, exception.toString())
            } finally {
                httpUrlConnection?.disconnect()
            }
            // Parse the JSON-formatted response
            return parseXmlString(data)
        }


        override fun onPostExecute(result: List<String>) {

            if (null != mListener.get()) {
                mListener.get()?.onDownloadFinished(result)
            }
        }

        private fun readStream(inputStream: InputStream): String {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val sep = System.getProperty("line.separator")
            val data = StringBuilder()
            try {
                reader.forEachLine {
                    Log.i(TAG, "Reading from socket")
                    data.append(it + sep)
                }
            } catch (e: IOException) {
                Log.e(TAG, "IOException")
            } finally {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e(TAG, "IOException")
                }
            }
            return data.toString()
        }

        private fun parseXmlString(data: String?): List<String>? {

            try {

                // Create the Pull Parser
                val factory = XmlPullParserFactory.newInstance()
                val xpp = factory.newPullParser()

                xpp.setInput(StringReader(data!!))

                // Get the first Parser event and start iterating over the XML document
                var eventType = xpp.eventType

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    when (eventType) {
                        XmlPullParser.START_TAG -> startTag(xpp.name)
                        XmlPullParser.END_TAG -> endTag(xpp.name)
                        XmlPullParser.TEXT -> text(xpp.text)
                    }
                    eventType = xpp.next()
                }
                return mResults
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        private fun startTag(localName: String) {
            when (localName) {
                LATITUDE_TAG -> mIsParsingLat = true
                LONGITUDE_TAG -> mIsParsingLng = true
                MAGNITUDE_TAG -> mIsParsingMag = true
            }
        }

        private fun text(text: String) {
            when {
                mIsParsingLat -> mLat = text.trim { it <= ' ' }
                mIsParsingLng -> mLng = text.trim { it <= ' ' }
                mIsParsingMag -> mMag = text.trim { it <= ' ' }
            }
        }

        private fun endTag(localName: String) {
            when (localName) {
                LATITUDE_TAG -> mIsParsingLat = false
                LONGITUDE_TAG -> mIsParsingLng = false
                MAGNITUDE_TAG -> mIsParsingMag = false
                EARTHQUAKE_TAG -> {
                    mResults.add(
                        MAGNITUDE_TAG + ":" + mMag + "," + LATITUDE_TAG + ":"
                                + mLat + "," + LONGITUDE_TAG + ":" + mLng
                    )
                    mLat = null
                    mLng = null
                    mMag = null
                }
            }
        }
    }
}
