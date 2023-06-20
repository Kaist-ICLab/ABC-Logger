package kaist.iclab.abclogger.collector.notification_event

class NotificationEventRepository(private val notificationEventDao: NotificationEventDao) {
    suspend fun insert(notificationEvent: NotificationEvent) = notificationEventDao.insert(notificationEvent)
}