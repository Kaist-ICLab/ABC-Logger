package kaist.iclab.abclogger.collector.screen_event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface ScreenEventDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(notificationEvent: ScreenEvent)
}