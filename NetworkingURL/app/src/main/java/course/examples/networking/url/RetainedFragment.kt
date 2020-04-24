package course.examples.networking.url

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class RetainedFragment : Fragment() {

    companion object {
        const val TAG = "RetainedFragment"
    }

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    internal fun onButtonPressed() {
        HttpGetTask(this).execute()
    }

    private fun onDownloadFinished(result: String) {
        if (null != mListener) {
            mListener!!.onDownloadfinished(result)
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
        fun onDownloadfinished(result: String)
    }

    internal class HttpGetTask(retainedFragment: RetainedFragment) :
        AsyncTask<Void, Void, String>() {

        companion object {

            private const val TAG = "HttpGetTask"

            // Get your own user name at http://www.geonames.org/login
            private const val USER_NAME = "aporter"

            private const val HOST = "api.geonames.org"
            private const val URL =
                ("http://" + HOST + "/earthquakesJSON?north=44.1&south=-9.9&east=-22.4&west=55.2&username="
                        + USER_NAME)
        }

        private val mListener: WeakReference<RetainedFragment> = WeakReference(retainedFragment)

        override fun doInBackground(vararg params: Void): String? {
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
            return data
        }

        override fun onPostExecute(result: String) {
            mListener.get()?.onDownloadFinished(result)
        }

        private fun readStream(inputStream: InputStream): String {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val data = StringBuilder()
            val sep = System.getProperty("line.separator")
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
    }
}
