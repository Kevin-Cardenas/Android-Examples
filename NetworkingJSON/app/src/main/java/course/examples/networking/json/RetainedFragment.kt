package course.examples.networking.json

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

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
        mListener?.onDownloadfinished()
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
                ("http://" + HOST + "/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
                        + USER_NAME)
            const val LONGITUDE_TAG = "lng"
            const val LATITUDE_TAG = "lat"
            const val MAGNITUDE_TAG = "magnitude"
            const val EARTHQUAKE_TAG = "earthquakes"

        }

        private val mListener: WeakReference<RetainedFragment> = WeakReference(retainedFragment)

        override fun doInBackground(vararg params: Void): List<String> {

            var httpUrlConnection: HttpURLConnection? = null
            var data: String? = null

            try {
                // 1. Get connection. 2. Prepare request (URI)
                httpUrlConnection = URL(URL)
                    .openConnection() as HttpURLConnection

                // 3. This app does not use a request body
                // 4. Read the response
                val inputstream = BufferedInputStream(
                    httpUrlConnection.inputStream
                )
                data = readStream(inputstream)
            } catch (exception: MalformedURLException) {
                Log.e(TAG, "MalformedURLException")
            } catch (exception: IOException) {
                Log.e(TAG, "IOException")
            } finally {
                httpUrlConnection?.disconnect()
            }
            // Parse the JSON-formatted response
            return parseJsonString(data)
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
                Log.e(TAG, e.toString())
            } finally {
                try {
                    reader.close()
                } catch (e: IOException) {
                    Log.e(TAG, e.toString())
                }
            }
            return data.toString()
        }

        private fun parseJsonString(data: String?): List<String> {

            val result = ArrayList<String>()

            try {
                // Get top-level JSON Object - a Map
                val responseObject = JSONTokener(
                    data
                ).nextValue() as JSONObject

                // Extract value of "earthquakes" key -- a List
                val earthquakes = responseObject
                    .getJSONArray(EARTHQUAKE_TAG)

                // Iterate over earthquakes list
                for (idx in 0 until earthquakes.length()) {

                    // Get single earthquake mData - a Map
                    val earthquake = earthquakes.get(idx) as JSONObject

                    // Summarize earthquake mData as a string and add it to
                    // result
                    result.add(
                        MAGNITUDE_TAG + ":"
                                + earthquake.get(MAGNITUDE_TAG) + ","
                                + LATITUDE_TAG + ":"
                                + earthquake.getString(LATITUDE_TAG) + ","
                                + LONGITUDE_TAG + ":"
                                + earthquake.get(LONGITUDE_TAG)
                    )
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return result
        }
    }
}
