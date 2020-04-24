package course.examples.alarms.alarmcreate

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.widget.Toast

class AlarmCreateActivity : Activity() {

    private lateinit var mAlarmManager: AlarmManager
    private lateinit var mNotificationReceiverPendingIntent: PendingIntent
    private lateinit var mLoggerReceiverPendingIntent: PendingIntent

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main)

        // Get the AlarmManager Service
        mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        val mNotificationReceiverIntent = Intent(
            this@AlarmCreateActivity,
            AlarmNotificationReceiver::class.java
        )

        // Create an PendingIntent that holds the NotificationReceiverIntent
        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
            this@AlarmCreateActivity, 0, mNotificationReceiverIntent, 0
        )

        // Create an Intent to broadcast to the AlarmLoggerReceiver
        val mLoggerReceiverIntent = Intent(
            this@AlarmCreateActivity,
            AlarmLoggerReceiver::class.java
        )

        // Create PendingIntent that holds the mLoggerReceiverPendingIntent
        mLoggerReceiverPendingIntent = PendingIntent.getBroadcast(
            this@AlarmCreateActivity, 0, mLoggerReceiverIntent, 0
        )

    }

    fun onClickSetSingleAlarmButton(v: View) {

        // Set single alarm
        mAlarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            mNotificationReceiverPendingIntent
        )

        // Set single alarm to fire shortly after previous alarm
        mAlarmManager.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + JITTER,
            mLoggerReceiverPendingIntent
        )


        // Show Toast message
        Toast.makeText(
            applicationContext, "Single Alarm Set",
            Toast.LENGTH_LONG
        ).show()
    }


    fun onClickSetRepAlarmButton(v: View) {

        // Set repeating alarm
        mAlarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime(),
            REPEAT_INTERVAL,
            mNotificationReceiverPendingIntent
        )

        // Set repeating alarm to fire shortly after previous alarm
        mAlarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + JITTER,
            REPEAT_INTERVAL,
            mLoggerReceiverPendingIntent
        )

        // Show Toast message
        Toast.makeText(
            applicationContext, "Repeating Alarm Set",
            Toast.LENGTH_LONG
        ).show()
    }

    fun onClickCancelRepAlarmButton(v: View) {

        // Cancel all alarms using mNotificationReceiverPendingIntent
        mAlarmManager.cancel(mNotificationReceiverPendingIntent)

        // Cancel all alarms using mLoggerReceiverPendingIntent
        mAlarmManager.cancel(mLoggerReceiverPendingIntent)

        // Show Toast message
        Toast.makeText(
            applicationContext,
            "Repeating Alarms Cancelled", Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        private const val JITTER = 1000L
        private const val REPEAT_INTERVAL = 60 * 1000L
    }

}