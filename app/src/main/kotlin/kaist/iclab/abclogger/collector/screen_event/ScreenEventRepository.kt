package kaist.iclab.abclogger.collector.screen_event

class ScreenEventRepository(private val screenEventDao: ScreenEventDao) {
    suspend fun insert(screenEvent: ScreenEvent) = screenEventDao.insert(screenEvent)
}