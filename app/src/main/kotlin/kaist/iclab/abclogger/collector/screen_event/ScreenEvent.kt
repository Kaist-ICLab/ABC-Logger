package kaist.iclab.abclogger.collector.screen_event

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screenEvents")
data class ScreenEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val timestamp: Long,
    val utcOffset: Int,
    val status: Int, // 0:
)
