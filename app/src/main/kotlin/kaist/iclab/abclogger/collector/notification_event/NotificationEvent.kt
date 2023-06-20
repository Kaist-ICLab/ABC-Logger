package kaist.iclab.abclogger.collector.notification_event

import androidx.room.Entity

@Entity(tableName = "notificationEvents")
data class NotificationEvent(
    val timestamp: Long,
    val utcOffset: Int,
    val packageName: String,
    val status: Int, // 1: POSTED, 0: REMOVED
)
