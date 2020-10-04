package kaist.iclab.abclogger.collector.content

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.CallLog
import androidx.core.content.getSystemService
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import kaist.iclab.abclogger.BuildConfig
import kaist.iclab.abclogger.collector.*
import kaist.iclab.abclogger.commons.*
import kaist.iclab.abclogger.core.AbstractCollector
import kaist.iclab.abclogger.core.DataRepository
import java.util.concurrent.TimeUnit

class CallLogCollector(
    context: Context,
    name: String,
    qualifiedName: String,
    description: String,
    dataRepository: DataRepository
) : AbstractCollector<CallLogEntity>(
    context,
    qualifiedName,
    name,
    description,
    dataRepository
) {
    override val permissions: List<String> = listOf(
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS
    )

    override val setupIntent: Intent? = null

    private val intent by lazy {
        PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_CALL_LOG_SCAN_REQUEST,
                Intent(ACTION_CALL_LOG_SCAN_REQUEST),
                PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val alarmManager by lazy {
        context.getSystemService<AlarmManager>()!!
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            handleCallLogScanRequest()
        }
    }

    private val contentResolver by lazy { context.contentResolver }

    override fun isAvailable(): Boolean = true

    override fun getStatus(): Array<Info> = arrayOf()

    override suspend fun onStart() {
        val filter = IntentFilter().apply {
            addAction(ACTION_CALL_LOG_SCAN_REQUEST)
        }

        context.safeRegisterReceiver(receiver, filter)

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(20),
                TimeUnit.MINUTES.toMillis(30),
                intent
        )
    }

    override suspend fun onStop() {
        context.safeUnregisterReceiver(receiver)

        alarmManager.cancel(intent)
    }

    override suspend fun count(): Long = dataRepository.count<CallLogEntity>()

    override suspend fun flush(entities: Collection<CallLogEntity>) {
        dataRepository.remove(entities)
        recordsUploaded += entities.size
    }

    override suspend fun list(limit: Long): Collection<CallLogEntity> = dataRepository.find(0, limit)

    private fun handleCallLogScanRequest() = launch {
        val timestamp = System.currentTimeMillis()

        getRecentContents(
                contentResolver = contentResolver,
                uri = CallLog.Calls.CONTENT_URI,
                timeColumn = CallLog.Calls.DATE,
                columns = arrayOf(
                        CallLog.Calls.DATE,
                        CallLog.Calls.DURATION,
                        CallLog.Calls.NUMBER,
                        CallLog.Calls.TYPE,
                        CallLog.Calls.NUMBER_PRESENTATION,
                        CallLog.Calls.DATA_USAGE
                ),
                lastTime = lastTimeDataWritten.coerceAtLeast(timestamp - TimeUnit.HOURS.toMillis(12))
        ) { cursor ->
            val millis = cursor.getLongOrNull(0) ?: 0
            val number = cursor.getStringOrNull(2) ?: ""
            val contact = getContact(contentResolver, number) ?: Contact()

            val entity = CallLogEntity(
                    duration = cursor.getLongOrNull(1) ?: 0,
                    number = toHash(number, 4),
                    type = stringifyCallType(cursor.getIntOrNull(3)),
                    presentation = stringifyCallPresentation(cursor.getIntOrNull(4)),
                    dataUsage = cursor.getLongOrNull(5) ?: 0,
                    contactType = contact.contactType,
                    isStarred = contact.isStarred,
                    isPinned = contact.isPinned
            )
            put(entity, millis)
        }
    }

    companion object {
        private const val ACTION_CALL_LOG_SCAN_REQUEST = "${BuildConfig.APPLICATION_ID}.ACTION_CALL_LOG_SCAN_REQUEST"
        private const val REQUEST_CODE_CALL_LOG_SCAN_REQUEST = 0x11
    }
}