package kaist.iclab.abclogger.collector.app_usage_event

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appUsageEvents")
data class AppUsageEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val queriedAt: Long,
    val timestamp: Long,
    val utcOffset: Int,
    val packageName: String,
    val eventType: Int,
    val className: String?
)
