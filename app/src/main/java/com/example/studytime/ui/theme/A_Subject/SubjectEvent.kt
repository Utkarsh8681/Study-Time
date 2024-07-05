package com.example.studytime.ui.theme.A_Subject

import androidx.compose.ui.graphics.Color
import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.Subject
import com.example.studytime.ui.theme.Domain.model.Task

sealed class SubjectEvent {

    data object updateSubject : SubjectEvent()

    data object deleteSubject : SubjectEvent()

    data object deleteSession : SubjectEvent()

    data object updateProgress : SubjectEvent()

    data class onTaskIsCompleteChange(val task : Task) : SubjectEvent()
    data class onSubjectCardColorChange(val color : List<Color>) : SubjectEvent()
    data class onSubjectNameChange(val Subject : String) : SubjectEvent()
    data class onGoalStudyHoursChange(val hours : String) : SubjectEvent()
    data class onDeleteSessionButtonClick(val session : Session) : SubjectEvent()


}