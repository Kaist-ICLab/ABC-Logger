package kaist.iclab.abclogger.collector.app_usage_event

import androidx.room.Entity

@Entity(tableName = "appUsageEvents")
data class AppUsageEvent(
    val queriedAt: Long,
    val timestamp: Long,
    val utcOffset: Int,
    val packageName: String,
    val eventType: Int,
    val className: String?
)
