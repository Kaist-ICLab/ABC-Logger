package old.kaist.iclab.abclogger.ui_old.settings.survey

import android.app.Application
import android.webkit.URLUtil
import androidx.lifecycle.SavedStateHandle
import old.kaist.iclab.abclogger.structure.survey.Survey
import old.kaist.iclab.abclogger.collector_old.survey.SurveyCollector
import old.kaist.iclab.abclogger.ui_old.base.BaseViewModel
import old.kaist.iclab.abclogger.commons.AbcError
import old.kaist.iclab.abclogger.commons.HttpRequestError
import old.kaist.iclab.abclogger.structure.survey.SurveyConfiguration
import old.kaist.iclab.abclogger.commons.getHttp
import old.kaist.iclab.abclogger.ui_old.State
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SurveySettingViewModel(
    private val collector: SurveyCollector,
    savedStateHandle: SavedStateHandle,
    application: Application
) : BaseViewModel(savedStateHandle, application) {
    var baseScheduledDate: Long
        get() = collector.baseScheduleDate
        set(value) {
            collector.baseScheduleDate = value
        }

    suspend fun getConfigurations() = withContext(ioContext) {
        collector.configurations
    }

    suspend fun setConfigurations(configurations: List<SurveyConfiguration>) =
        withContext(ioContext) {
            collector.configurations = configurations
        }

    fun download(url: String?) = flow {
        emit(State.Loading)
        try {
            if (url.isNullOrBlank() || !URLUtil.isValidUrl(url)) throw HttpRequestError.invalidUrl()
            val response = getHttp(url) ?: throw HttpRequestError.emptyContent()
            val survey = Survey.fromJson(response) ?: throw HttpRequestError.invalidJsonFormat()

            emit(State.Success(survey))
        } catch (e: Exception) {
            emit(State.Failure(AbcError.wrap(e)))
        }
    }
}