package com.example.studytime.ui.theme.Dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studytime.ui.theme.A_Subject.SubjectEvent
import com.example.studytime.ui.theme.A_Subject.SubjectState
import com.example.studytime.ui.theme.Domain.model.Subject
import com.example.studytime.ui.theme.Domain.model.Task
import com.example.studytime.ui.theme.Domain.model.repository.SessionRepository
import com.example.studytime.ui.theme.Domain.model.repository.SubjectRepository
import com.example.studytime.ui.theme.Domain.model.repository.TaskRepository
import com.example.studytime.ui.theme.Util.SnackbarEvent
import com.example.studytime.ui.theme.Util.toHours
import com.example.studytime.ui.theme.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject



@HiltViewModel
class SubjectScreenViewModel @Inject constructor(

    private val subjectRepository : SubjectRepository,
    private val taskRepository : TaskRepository,
    private val sessionRepository : SessionRepository,
    savedStateHandle : SavedStateHandle


):ViewModel() {

    private val navArgs :SubjectScreenNavArgs  = savedStateHandle.navArgs()



    private val _state = MutableStateFlow(SubjectState())
    val state = combine(
        _state,
        taskRepository.getUpcomingTaskForSubject(navArgs.subjectId),
        taskRepository.getCompletedTaskForSubject(navArgs.subjectId),
        sessionRepository.getRecentFiveSessions(),
        sessionRepository.getTotalSessionDuration()

    ){
            state , upcomingTasks , completedTasks , recentSessions, totalSessionDuration  ->

        state.copy(
            upcomingTasks = upcomingTasks,
            completedTasks = completedTasks,
            recentSessions = recentSessions,
            studiedHours = totalSessionDuration.toHours()

        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectState()
    )

    private val _snackbarEventFlow = MutableSharedFlow<SnackbarEvent>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()


    init {
        fetchSubject()
        deleteSubject()
    }
    fun onEvent(event : SubjectEvent){
        when(event){
            is SubjectEvent.onSubjectCardColorChange -> {
                _state.update {
                    it.copy(subjectCardColors = event.color)
                }
            }
            is SubjectEvent.onSubjectNameChange ->{
                _state.update {
                    it.copy(subjectName = event.Subject)
                }
            }
            is SubjectEvent.onGoalStudyHoursChange -> {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }
            SubjectEvent.updateSubject -> UpdateSubject()
            SubjectEvent.deleteSession -> deleteSession()
            SubjectEvent.deleteSubject -> deleteSubject()
            is SubjectEvent.onDeleteSessionButtonClick ->{
                _state.update {
                    it.copy(session = event.session)
                }
            }
            is SubjectEvent.onTaskIsCompleteChange -> updateTask(event.task)
            SubjectEvent.updateProgress -> {
                val goalStudyHours = state.value.goalStudyHours.toFloatOrNull()?:1f
                _state.update {
                    it.copy(
                        progress = state.value.studiedHours/goalStudyHours.coerceIn(0f, 1f)
                    )
                }
            }
        }
    }

    private fun UpdateSubject() {

        viewModelScope.launch {
            try {


                subjectRepository.upsertSubject(
                    subject = Subject(
                        subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() }
                    )
                )
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar("Subject Updated Sucesfully")
                )
            }
            catch (
                e:Exception
            ){
                SnackbarEvent.ShowSnackbar("Could not update subject. ${e.message}")
                SnackbarDuration.Long
            }
        }
    }
    private fun updateTask(task : Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task = task.copy(
                        isComplete = !task.isComplete
                    )

                )
                if (task.isComplete) {
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(
                            "Saved in completed task."
                        )
                    )
                }else{
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(
                            "Saved in upcoming task."
                        )
                    )
                }
            }
            catch (e:Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(
                        "Couldn't update task. ${e.message}",
                        SnackbarDuration.Long
                    )
                )
            }
        }
    }


    private fun fetchSubject(){
        viewModelScope.launch {
            subjectRepository
                .getSubjectById(navArgs.subjectId)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectName = subject.name,
                            goalStudyHours = subject.goalHours.toString(),
                            subjectCardColors = subject.colors.map { Color(it) },
                            currentSubjectId = subject.subjectId
                        )
                    }
                }
        }
    }

    private fun deleteSubject(){
        viewModelScope.launch {

            try {
                val currentSubjectId = state.value.currentSubjectId
                if(currentSubjectId != null) {
                    withContext(Dispatchers.IO){

                        subjectRepository.deleteSubject(subjectId = currentSubjectId)
                    }

                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "Subject deleted sucessfully")

                    )
                    _snackbarEventFlow.emit(SnackbarEvent.NavigateUp)
                }
                else{
                    _snackbarEventFlow.emit(
                        SnackbarEvent.ShowSnackbar(message = "No subject to delete")
                    )
                }
            } catch (e:Exception){
                _snackbarEventFlow.emit(
                    SnackbarEvent.ShowSnackbar(message = "Could not delete subject. ${e.message}" ,
                        duration = SnackbarDuration.Long
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

}