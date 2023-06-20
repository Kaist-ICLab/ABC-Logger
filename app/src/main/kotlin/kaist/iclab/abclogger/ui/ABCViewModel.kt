package kaist.iclab.abclogger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kaist.iclab.abclogger.ABCApplication
import kaist.iclab.abclogger.collector.app_usage_event.AppUsageEventCollector
import kaist.iclab.abclogger.collector.screen_event.ScreenEventCollector
import org.koin.androidx.compose.get

class ABCViewModel(
    private val appUsageEventCollector: AppUsageEventCollector,
    private val screenEventCollector: ScreenEventCollector
):ViewModel() {

    fun startCollect(){
        appUsageEventCollector.start()
        screenEventCollector.start()
    }
    fun stopCollect() {
        appUsageEventCollector.stop()
        screenEventCollector.stop()
    }
}