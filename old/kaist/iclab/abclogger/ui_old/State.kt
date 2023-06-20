package old.kaist.iclab.abclogger.ui_old

sealed class State {
    object Loading : State()
    class Success<T>(val data: T) : State()
    class Failure(val error: Throwable?) : State()
}
