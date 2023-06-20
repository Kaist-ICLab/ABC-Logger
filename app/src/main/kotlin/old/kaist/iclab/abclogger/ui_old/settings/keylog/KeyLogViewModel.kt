package old.kaist.iclab.abclogger.ui_old.settings.keylog

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import old.kaist.iclab.abclogger.collector_old.keylog.KeyLogCollector
import old.kaist.iclab.abclogger.ui_old.base.BaseViewModel

class KeyLogViewModel(
    private val collector: KeyLogCollector,
    savedStateHandle: SavedStateHandle,
    application: Application
) : BaseViewModel(savedStateHandle, application) {
    var keyboardType
        get() = collector.keyboardType
        set(value) {
            collector.keyboardType = value
        }

    fun isAccessibilityServiceAllowed() = collector.isAccessibilityServiceRunning()

}