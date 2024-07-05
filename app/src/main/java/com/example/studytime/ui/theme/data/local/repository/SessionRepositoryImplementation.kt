package com.example.studytime.ui.theme.data.local.repository

import com.example.studytime.tasks
import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.repository.SessionRepository
import com.example.studytime.ui.theme.data.local.local.SessionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class SessionRepositoryImplementation @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository {
    override suspend fun insertSession(session: Session) {
        sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    override fun getAllSessions(): Flow<List<Session>> {
        return sessionDao.getAllSessions()
            .map { sessions -> sessions.sortedByDescending { it.date } }
    }

    override fun getRecentFiveSessions() : Flow<List<Session>> {
        return sessionDao.getAllSessions()
            .take(count = 5)
            .map { sessions -> sessions.sortedByDescending { it.date } }
    }

    override fun getRecentSessionForSubject(subjectId: Int): Flow<List<Session>> {
       return sessionDao.getRecentSessionForSubject(subjectId)
           .map { sessions -> sessions.sortedByDescending { it.date } }
           .take(count = 10)
    }

    override fun getTotalSessionDuration(): Flow<Long> {
       return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationBySubjectId(subjectId: Int): Flow<Long> {
        return sessionDao.getTotalSessionDurationBySubjectId(subjectId)
    }

    override fun deleteSessionBySubjectId(subjectId: Int) {
        TODO("Not yet implemented")
    }
}