package course.examples.broadcastreceiver.compoundorderedbroadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class Receiver1 : BroadcastReceiver() {

    companion object {
        private const val TAG = "Receiver1"

    }

    override fun onReceive(context: Context, intent: Intent) {

        Log.i(TAG, "INTENT RECEIVED")

        if (isOrderedBroadcast) {
            Log.i(TAG, "Calling abortBroadcast()")
            abortBroadcast()
        }
    }
}
