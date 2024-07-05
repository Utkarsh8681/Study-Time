package com.example.studytime.ui.theme.Domain.model.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.studytime.ui.theme.Domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun insertSession(session: Session)


    suspend fun deleteSession(session: Session)


    fun getAllSessions() : Flow<List<Session>>

    fun getRecentFiveSessions() : Flow<List<Session>>

    fun getRecentSessionForSubject(subjectId : Int) : Flow<List<Session>>


    fun getTotalSessionDuration() : Flow<Long>


    fun getTotalSessionDurationBySubjectId(subjectId: Int) : Flow<Long>


    fun deleteSessionBySubjectId(subjectId: Int)
}