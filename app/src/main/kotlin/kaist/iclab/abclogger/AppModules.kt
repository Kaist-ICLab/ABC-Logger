package kaist.iclab.abclogger

import kaist.iclab.abclogger.collector.app_usage_event.AppUsageEventRepository
import kaist.iclab.abclogger.collector.notification_event.NotificationEventRepository
import kaist.iclab.abclogger.collector.screen_event.ScreenEventRepository
import kaist.iclab.abclogger.data.ABCDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModules = module {
    single(createdAtStart = true) {
        AppUsageEventRepository(ABCDatabase.getDatabase(androidContext()).appUsageDao())
    }
    single(createdAtStart = true) {
        ScreenEventRepository(ABCDatabase.getDatabase(androidContext()).screenDao())
    }
    single(createdAtStart = true) {
        NotificationEventRepository(ABCDatabase.getDatabase(androidContext()).notificationDao())
    }
}
val collectorModules = module {

}

//val viewModelModules = module {
//    viewModel { (handle: SavedStateHandle) ->
//        ConfigViewModel(
//            savedStateHandle = handle,
//            collectorRepository = get(),
//            dataRepository = get(),
//            application = androidApplication()
//        )
//    }
//    viewModel {(handle: SavedStateHandle) ->
//        SurveyViewModel(
//            savedStateHandle = handle,
//            dataRepository = get(),
//            application = androidApplication()
//        )
//    }
//    viewModel {(handle: SavedStateHandle) ->
//        PolarH10ViewModel(
//            savedStateHandle = handle,
//            collector = get(),
//            application = androidApplication()
//        )
//    }
//    viewModel {(handle: SavedStateHandle) ->
//        SurveySettingViewModel(
//            savedStateHandle = handle,
//            collector = get(),
//            application = androidApplication()
//        )
//    }
//    viewModel {(handle: SavedStateHandle) ->
//        KeyLogViewModel(
//            savedStateHandle = handle,
//            collector = get(),
//            application = androidApplication()
//        )
//    }
//}
