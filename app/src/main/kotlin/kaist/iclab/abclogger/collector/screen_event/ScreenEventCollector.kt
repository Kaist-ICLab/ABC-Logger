package kaist.iclab.abclogger.collector.screen_event

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import kaist.iclab.abclogger.collector.Collector
import kaist.iclab.abclogger.collector.goAsync
import old.kaist.iclab.abclogger.commons.safeUnregisterReceiver
import java.util.TimeZone

/*
* Screen Event can be collected by Broadcast Receiver by capturing actions:
*  Intent.ACTION_SCREEN_ON | Intent.ACTION_SCREEN_OFF | Intent.ACTION_USER_PRESENT | Intent.ACTION_USER_UNLOCKED
* */
class ScreenEventCollector(
    val context: Context,
    val screenEventRepository: ScreenEventRepository): Collector {

    override fun isAvailable(): Boolean = true

    override fun start() {
        Log.d(javaClass.name, "start()")
        context.registerReceiver(receiver, filter)
    }

    override fun stop() {
        Log.d(javaClass.name, "stop()")
        context.safeUnregisterReceiver(receiver)
    }

    private val receiver: BroadcastReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) = goAsync {
            Log.d(javaClass.name, "onReceive()")
            intent?.action?.let{
                handleRetrieval(it)
            }
        }
    }

    private val filter by lazy{
        IntentFilter().apply{
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_USER_UNLOCKED)
        }
    }


    private suspend fun handleRetrieval(action: String) {
        val currentTimestamp = System.currentTimeMillis()
        val utcOffset = TimeZone.getDefault().rawOffset
        try{
            screenEventRepository.insert(
                ScreenEvent(
                    timestamp =  currentTimestamp,
                    utcOffset = utcOffset,
                    status = SCREEN_EVENT.valueOf(action).ordinal
            )
            )
        }catch (throwable: Throwable){
            Log.e(javaClass.name, "handleRetrieval(): $throwable")
        }
    }
}

enum class SCREEN_EVENT(action: String) {
    ON(action = Intent.ACTION_SCREEN_ON),
    OFF(action = Intent.ACTION_SCREEN_OFF),
    USER_PRESENT(action = Intent.ACTION_USER_PRESENT),
    USER_UNLOCKED(action = Intent.ACTION_USER_UNLOCKED)
}