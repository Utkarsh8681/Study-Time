package com.example.studytime.ui.theme.session

import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.Subject

sealed class SessionEvent {
    data class onRelatedSubjectChange(val subject : Subject):SessionEvent()

    data class saveSession(val duration : Long):SessionEvent()

    data class onDeleteSessionButtinClick(val session : Session):SessionEvent()

    data object deleteSession:SessionEvent()

    data object NotifyToUpdateSubject:SessionEvent()

    data class UpdateSubjectIdAndRelatedSubject(
        val subjectId : Int?,
        val relatedSubject : String?
    ):SessionEvent()



}