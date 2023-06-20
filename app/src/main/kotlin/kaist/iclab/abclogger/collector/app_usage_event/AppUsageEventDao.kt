package kaist.iclab.abclogger.collector.app_usage_event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy


@Dao
interface AppUsageEventDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(appUsageEvent: AppUsageEvent)
}