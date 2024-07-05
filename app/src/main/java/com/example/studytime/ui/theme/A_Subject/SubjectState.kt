package com.example.studytime.ui.theme.A_Subject

import android.graphics.Color
import android.provider.CalendarContract.Colors
import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.Subject
import com.example.studytime.ui.theme.Domain.model.Task

data class SubjectState(
    val currentSubjectId :Int? = null,
    val subjectName : String = "",
    val goalStudyHours : String = "",
    val studiedHours :Float = 0f,
    val upcomingTasks : List<Task> = emptyList(),
    val completedTasks : List<Task> = emptyList(),
    val recentSessions : List<Session> = emptyList(),
    val subjectCardColors : List<androidx.compose.ui.graphics.Color> = Subject.subjectCardColors.random(),
    val session :Session? = null,
    val progress :Float = 0f,

)