package kaist.iclab.abclogger.collector.screen_event

import androidx.room.Entity

@Entity(tableName = "screenEvents")
data class ScreenEvent(
    val timestamp: Long,
    val utcOffset: Int,
    val status: Int, // 0:
)
