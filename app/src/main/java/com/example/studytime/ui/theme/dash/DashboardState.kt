package com.example.studytime.ui.theme.dash

import android.provider.CalendarContract.Colors
import androidx.compose.ui.graphics.Color
import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.Subject

data class DashboardState(
    val totalSubjectCount : Int = 0,
    val totalStudiedHours : Float = 0f,
    val totalGoalStudiedHours : Float = 0f,
    val subjects :List<Subject> = emptyList(),
    val subjectName : String ="",
    val goalStudyHours : String ="",
    val subjectCardColors : List<Color> = Subject.subjectCardColors.random(),
    val session : Session? = null

)
