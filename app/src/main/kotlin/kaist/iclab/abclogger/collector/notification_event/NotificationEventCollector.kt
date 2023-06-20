package kaist.iclab.abclogger.collector.notification_event

import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import kaist.iclab.abclogger.collector.Collector
import java.util.TimeZone

/* NotificationEvent can be collected by using android.service.notification.NotificationListenerService
* Notification Event requires users to disable notification on their own...
* We will explicitly not record notification by changing isRecording
* */
class NotificationEventCollector(
    private val context: Context,
    private val notificationEventRepository: NotificationEventRepository
    ): Collector {

    private var isRecording = false

    private val notificationEventListener: NotificationEventListener by lazy {
        NotificationEventListener(
            onNotificationRetrieved = {sbn, timestamp, notificationStatus ->  })
    }

    override fun isAvailable(): Boolean =
        context.packageName in NotificationManagerCompat.getEnabledListenerPackages(context)

    suspend fun onNotificationRetrieved(sbn: StatusBarNotification, timestamp: Long, notificationStatus: NotificationStatus){
        if(isRecording){
            notificationEventRepository.insert(
                NotificationEvent(
                    timestamp = timestamp,
                    utcOffset = TimeZone.getDefault().rawOffset,
                    packageName = sbn.packageName,
                    status = notificationStatus.ordinal
                ))
        }
    }

    override fun start() {
        isRecording = true
    }

    override fun stop() {
        isRecording = false
    }

}

class NotificationEventListener(
    val onNotificationRetrieved: (sbn:StatusBarNotification, timestamp: Long, notificationStatus: NotificationStatus) -> Unit): NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.let{
            onNotificationRetrieved(sbn, System.currentTimeMillis(),NotificationStatus.POSTED)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        sbn?.let{
            onNotificationRetrieved(sbn, System.currentTimeMillis(), NotificationStatus.REMOVED)
        }
    }
}


enum class NotificationStatus {
    POSTED,
    REMOVED
}