package kaist.iclab.abclogger.base

import android.app.Application
import androidx.lifecycle.*
import kaist.iclab.abclogger.ui.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel(
    private val savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {
    protected val ioContext = viewModelScope.coroutineContext + Dispatchers.IO

    protected val uiContext = viewModelScope.coroutineContext + Dispatchers.Main

    protected fun launchIo(block: suspend () -> Unit) = viewModelScope.launch(Dispatchers.IO) { block() }

    protected fun launchUi(block: suspend () -> Unit) = viewModelScope.launch(Dispatchers.Main) { block() }

    fun <T> saveState(key: String, state: T) = savedStateHandle.set(key, state)

    fun <T> loadState(key: String) = savedStateHandle.get<T>(key)
}