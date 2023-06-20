package old.kaist.iclab.abclogger.ui_old.survey

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import old.kaist.iclab.abclogger.core_old.DataRepository
import old.kaist.iclab.abclogger.ui_old.base.BaseViewModel
import old.kaist.iclab.abclogger.collector_old.survey.*
import old.kaist.iclab.abclogger.commons.AbcError
import old.kaist.iclab.abclogger.commons.EntityError
import old.kaist.iclab.abclogger.core_old.AuthRepository
import old.kaist.iclab.abclogger.core_old.EventBus
import old.kaist.iclab.abclogger.core_old.Log
import old.kaist.iclab.abclogger.structure.survey.Survey
import old.kaist.iclab.abclogger.ui_old.State
import old.kaist.iclab.abclogger.ui_old.survey.list.SurveyPagingSource
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class SurveyViewModel(
    private val dataRepository: DataRepository,
    savedStateHandle: SavedStateHandle,
    application: Application
) : BaseViewModel(savedStateHandle, application) {
    private val saveStatusChannel = Channel<State>()

    val saveStateFlow = saveStatusChannel.receiveAsFlow()

    fun get(id: Long) = flow {
        try {
            val survey =
                dataRepository.get<InternalSurveyEntity>(id) ?: throw EntityError.notFound()
            survey.responses = dataRepository.find {
                equal(InternalResponseEntity_.surveyId, id)
            }
            emit(State.Success(survey))
        } catch (e: Exception) {
            emit(State.Failure(AbcError.wrap(e)))
        }
    }

    suspend fun listAll() : Flow<PagingData<InternalSurveyEntity>> {
        val curTime = System.currentTimeMillis()
        prepareSync(curTime)

        return Pager(PagingConfig(10)) {
            SurveyPagingSource(dataRepository) {
                greater(InternalSurveyEntity_.actualTriggerTime, 0)
                less(InternalSurveyEntity_.intendedTriggerTime, curTime)
                orderDesc(InternalSurveyEntity_.intendedTriggerTime)
            }
        }.flow
    }

    suspend fun listAnswered() : Flow<PagingData<InternalSurveyEntity>> {
        val curTime = System.currentTimeMillis()
        prepareSync(curTime)

        return Pager(PagingConfig(10)) {
            SurveyPagingSource(dataRepository) {
                greater(InternalSurveyEntity_.actualTriggerTime, 0)
                //equal(InternalSurveyEntity_.isTransferredToSync, true)    //may needed
                greater(InternalSurveyEntity_.responseTime, 0)
                orderDesc(InternalSurveyEntity_.actualTriggerTime)
            }
        }.flow
    }

    suspend fun listNotAnswered() : Flow<PagingData<InternalSurveyEntity>> {
        val curTime = System.currentTimeMillis()
        prepareSync(curTime)

        return Pager(PagingConfig(10)) {
            SurveyPagingSource(dataRepository) {
                greater(InternalSurveyEntity_.actualTriggerTime, 0)
                less(InternalSurveyEntity_.intendedTriggerTime, curTime)
                    .and()
                    .less(InternalSurveyEntity_.responseTime, 0)
                orderDesc(InternalSurveyEntity_.intendedTriggerTime)
            }
        }.flow
    }

    suspend fun listExpired() : Flow<PagingData<InternalSurveyEntity>> {
        val curTime = System.currentTimeMillis()
        prepareSync(curTime)

        return Pager(PagingConfig(10)) {
            SurveyPagingSource(dataRepository) {
                greater(InternalSurveyEntity_.actualTriggerTime, 0)
                //equal(InternalSurveyEntity_.isTransferredToSync, false)
                less(InternalSurveyEntity_.timeoutUntil, curTime)
                equal(
                    InternalSurveyEntity_.timeoutAction,
                    Survey.TimeoutAction.DISABLED.ordinal.toLong()
                )
                orderDesc(InternalSurveyEntity_.intendedTriggerTime)
            }
        }.flow
    }

    fun post(
        id: Long,
        responses: List<InternalResponseEntity>,
        reactionTime: Long,
        responseTime: Long
    ) = GlobalScope.launch(Dispatchers.IO) {
        try {
            saveStatusChannel.send(State.Loading)

            val survey =
                dataRepository.get<InternalSurveyEntity>(id) ?: throw EntityError.notFound()

            val updatedSurvey = survey.copy(
                isTransferredToSync = true,
                responseTime = responseTime,
                reactionTime = reactionTime,
            )

            dataRepository.put(updatedSurvey)
            responses.forEach { dataRepository.put(it) }

            val answeredSurveyEntity = toSurveyEntity(updatedSurvey, responses)
            answeredSurveyEntity.apply {
                utcOffset = TimeZone.getDefault().rawOffset / 1000
                groupName = AuthRepository.groupName
                email = AuthRepository.email
                instanceId = AuthRepository.instanceId
                source = AuthRepository.source
                deviceManufacturer = AuthRepository.deviceManufacturer
                deviceModel = AuthRepository.deviceModel
                deviceVersion = AuthRepository.deviceVersion
                deviceOs = AuthRepository.deviceOs
                appId = AuthRepository.appId
                appVersion = AuthRepository.appVersion
            }
            dataRepository.put(answeredSurveyEntity)
            EventBus.post(answeredSurveyEntity)
            Log.d(javaClass, answeredSurveyEntity)
            saveStatusChannel.send(State.Success(Unit))
        } catch (e: Exception) {
            saveStatusChannel.send(State.Failure(AbcError.wrap(e)))
        }
    }

    private suspend fun prepareSync(timestamp: Long) = withContext(ioContext) {
        try {
            saveStatusChannel.send(State.Loading)

            val expiredEntities = dataRepository.find<InternalSurveyEntity> {
                greater(InternalSurveyEntity_.actualTriggerTime, 0)
                equal(InternalSurveyEntity_.isTransferredToSync, false)
                less(InternalSurveyEntity_.timeoutUntil, timestamp)
                equal(
                    InternalSurveyEntity_.timeoutAction,
                    Survey.TimeoutAction.DISABLED.ordinal.toLong()
                )
            }.map { survey ->
                val responses = dataRepository.find<InternalResponseEntity> {
                    equal(InternalResponseEntity_.surveyId, survey.id)
                }
                toSurveyEntity(survey, responses)
            }
            expiredEntities.forEach {
                it.apply {
                    utcOffset = TimeZone.getDefault().rawOffset / 1000
                    groupName = AuthRepository.groupName
                    email = AuthRepository.email
                    instanceId = AuthRepository.instanceId
                    source = AuthRepository.source
                    deviceManufacturer = AuthRepository.deviceManufacturer
                    deviceModel = AuthRepository.deviceModel
                    deviceVersion = AuthRepository.deviceVersion
                    deviceOs = AuthRepository.deviceOs
                    appId = AuthRepository.appId
                    appVersion = AuthRepository.appVersion
                }
                dataRepository.put(it)
                EventBus.post(it)
                Log.d(javaClass, it)
            }
            saveStatusChannel.send(State.Success(Unit))
        } catch (e: Exception) {
            saveStatusChannel.send(State.Failure(AbcError.wrap(e)))
        }
    }

    /**
     * The function toSurveyEntity makes the actual SurveyEntity to be uploaded into the DB.
     */
    private fun toSurveyEntity(
        survey: InternalSurveyEntity,
        responses: List<InternalResponseEntity>
    ) = SurveyEntity(
            eventTime = survey.eventTime,
            eventName = survey.uuid,        // TODO: temporary changed. the original was survey.eventName
            intendedTriggerTime = survey.intendedTriggerTime,
            actualTriggerTime = survey.actualTriggerTime,
            reactionTime = survey.reactionTime,
            responseTime = survey.responseTime,
            url = survey.url,
            title = survey.title.main,
            altTitle = survey.title.alt,
            message = survey.message.main,
            altMessage = survey.message.alt,
            instruction = survey.instruction.main,
            altInstruction = survey.instruction.alt,
            timeoutUntil = survey.timeoutUntil,
            timeoutAction = survey.timeoutAction.name,
            responses = responses.map { response ->
                SurveyEntity.Response(
                    index = response.index,
                    type = response.question.option.type.name,
                    question = response.question.title.main,
                    altQuestion = response.question.title.alt,
                    answer = response.answer.main + response.answer.other
                )
            }
        ).apply {
            this.timestamp = System.currentTimeMillis()
            //id = survey.id                      // error: ID is higher or equal to internal ID sequence: 1 (vs. 1). Use ID 0 (zero) to insert new entities.
            //instanceId = survey.id.toString()   // instanceId is uuid for each participant defined by AuthRepository.
        }
}
