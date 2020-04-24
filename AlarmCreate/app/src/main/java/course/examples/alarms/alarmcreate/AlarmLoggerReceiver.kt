package course.examples.alarms.alarmcreate

import java.text.DateFormat
import java.util.Date

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmLoggerReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmLoggerReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Log receipt of the Intent with timestamp
        Log.i(TAG, context.getString(R.string.logging_at_string) +
                DateFormat.getDateTimeInstance().format(Date()))
    }
}
