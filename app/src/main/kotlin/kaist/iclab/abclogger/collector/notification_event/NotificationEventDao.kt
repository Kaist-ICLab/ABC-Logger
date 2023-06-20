package kaist.iclab.abclogger.collector.notification_event

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface NotificationEventDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(notificationEvent: NotificationEvent)
}