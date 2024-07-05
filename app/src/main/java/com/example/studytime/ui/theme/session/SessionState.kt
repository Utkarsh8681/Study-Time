package com.example.studytime.ui.theme.session

import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.Subject

data class SessionState(
    val subjects : List<Subject> = emptyList(),
    val sessions : List<Session> = emptyList(),
    val relatedToSubject : String? = null,
    val subjectId : Int? = null,
    val session : Session? = null
)

