package kaist.iclab.abclogger.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kaist.iclab.abclogger.collector.app_usage_event.AppUsageEvent
import kaist.iclab.abclogger.collector.app_usage_event.AppUsageEventDao
import kaist.iclab.abclogger.collector.notification_event.NotificationEvent
import kaist.iclab.abclogger.collector.notification_event.NotificationEventDao
import kaist.iclab.abclogger.collector.screen_event.ScreenEvent
import kaist.iclab.abclogger.collector.screen_event.ScreenEventDao
//NotificationEvent::class,
@Database(version = 1, entities = [AppUsageEvent::class, ScreenEvent::class])
abstract class ABCDatabase: RoomDatabase() {
    abstract fun appUsageDao(): AppUsageEventDao
//    abstract fun notificationDao(): NotificationEventDao
    abstract fun screenDao(): ScreenEventDao

    companion object {
        @Volatile
        private var instance: ABCDatabase? = null

        fun getDatabase(context: Context): ABCDatabase {
            return instance ?: synchronized(this){
                Room.databaseBuilder(context, ABCDatabase::class.java, "abc_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also{ instance = it}
            }
        }
    }
}