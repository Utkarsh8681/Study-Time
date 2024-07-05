package com.example.studytime.ui.theme.Dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.repository.SessionRepository
import com.example.studytime.ui.theme.Domain.model.repository.SubjectRepository
import com.example.studytime.ui.theme.Util.SnackbarEvent
import com.example.studytime.ui.theme.session.SessionEvent
import com.example.studytime.ui.theme.session.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject



@HiltViewModel
class SessionViewModel @Inject constructor(
    subjectRepository : SubjectRepository,
    private val sessionRepository : SessionRepository
):ViewModel() {

    private val _state = MutableStateFlow(SessionState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects(),
        sessionRepository.getAllSessions()
    ){
        state, subjects, sessions ->
        state.copy(
            subjects = subjects,
            sessions = sessions
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SessionState()
    )
    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()


    fun onEvent(event : SessionEvent){
        when(event){
            is SessionEvent.UpdateSubjectIdAndRelatedSubject -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.relatedSubject,
                        subjectId = event.subjectId
                    )
                }
            }
            SessionEvent.NotifyToUpdateSubject -> notifyUpdateSubject()
            SessionEvent.deleteSession -> deleteSession()
            is SessionEvent.onDeleteSessionButtinClick ->{
                _state.update {
                    it.copy(session = event.session)
                }
            }
            is SessionEvent.onRelatedSubjectChange -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }
            is SessionEvent.saveSession -> insertSession(event.duration)
        }
    }

    private fun notifyUpdateSubject() {

        viewModelScope.launch{
            if (state.value.subjectId == null || state.value.relatedToSubject == null){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Please select subject related to the session",
                        SnackbarDuration.Long
                    )
                )
            }

        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                }
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "session Deleted successfully.",
                        SnackbarDuration.Long
                    )
                )
            } catch (e : Exception) {
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't delete session . ${e.message}",
                        SnackbarDuration.Long
                    )
                )
            }
        }
    }
    private fun insertSession(duration : Long) {

        viewModelScope.launch{
            if(duration < 36){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Session Can't be less than 36seconds.",
                        SnackbarDuration.Long
                    )
                )
                return@launch
            }
            try {
                sessionRepository.insertSession(
                    session = Session(
                        sessionSubjectId = state.value.subjectId ?: -1,
                        relatedToSubject = state.value.relatedToSubject ?: "",
                        date = Instant.now().toEpochMilli(),
                        duration = duration
                    )
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Session Saved Successfully .",
                        SnackbarDuration.Long
                    )
                )

            }catch (e:Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't save session . ${e.message}",
                        SnackbarDuration.Long
                    )
                )
            }

        }
    }
}