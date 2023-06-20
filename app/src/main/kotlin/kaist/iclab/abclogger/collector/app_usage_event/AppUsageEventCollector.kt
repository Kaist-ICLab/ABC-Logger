package kaist.iclab.abclogger.collector.app_usage_event

import android.app.AlarmManager
import android.app.AppOpsManager
import android.app.PendingIntent
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Process
import android.util.Log
import androidx.core.content.getSystemService
import kaist.iclab.abclogger.collector.Collector
import kaist.iclab.abclogger.collector.goAsync
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/* AppUsageEvent can be queried from android.app.usage.UsageStatsManager
* However, UsageStatsManager does not provide any event-triggered Listener Service
* Therefore, we will do the sampling regularly using alarmManager
* Theoretically, sampling rate of alarmManager will not affect the Quality of Data*/
class AppUsageEventCollector(
    val context: Context,
    val appUsageEventRepository: AppUsageEventRepository):Collector {

    companion object{
        private const val ACTION_CODE = "ACTION_COLLECT_APP_USAGE_EVENT"
        private const val REQUEST_CODE = 0xee
    }

    private var previousTimestamp = 0L

    private val alarmManager by lazy {
        context.getSystemService<AlarmManager>()!!
    }

    private val intent by lazy {
        PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(ACTION_CODE),
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private val filter by lazy{
        IntentFilter().apply{
            addAction(ACTION_CODE)
        }
    }

    private val receiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) = goAsync {
            Log.d(javaClass.name, "onReceive()")
            handleRetrieval()
        }
    }
    private suspend fun handleRetrieval() {
        val currentTimestamp = System.currentTimeMillis()
        val utcOffset = TimeZone.getDefault().rawOffset
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val usageStatsList = usageStatsManager.queryEvents(previousTimestamp, currentTimestamp)
        val event: UsageEvents.Event = UsageEvents.Event()
        try{
            while(usageStatsList.getNextEvent(event)){
                appUsageEventRepository.insert(event.toAppUsageEvent(
                    queriedAt = currentTimestamp, utcOffset = utcOffset))
            }
            previousTimestamp = currentTimestamp
        }catch (throwable: Throwable){
            Log.e(javaClass.name, "handleRetrieval(): $throwable")
        }
    }

    override fun isAvailable():Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
        } else {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName
            )
        } == AppOpsManager.MODE_ALLOWED
    }

    override fun start() {
        Log.d(javaClass.name, "start()")
        context.registerReceiver(
            receiver,
            filter
        )
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15),
            TimeUnit.MINUTES.toMillis(30),
            intent
        )
    }

    override fun stop() {
        context.unregisterReceiver(receiver)
        alarmManager.cancel(intent)
    }
}

fun UsageEvents.Event.toAppUsageEvent(queriedAt: Long, utcOffset: Int): AppUsageEvent {
    return AppUsageEvent(
        queriedAt= queriedAt,
        timestamp = this.timeStamp,
        utcOffset = utcOffset,
        packageName = this.packageName,
        eventType = this.eventType,
        className = this.className
    )
}