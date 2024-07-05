package com.example.studytime.ui.theme.data.local.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query("SELECT * FROM SESSION")
    fun getAllSessions() : Flow<List<Session>>

    @Query("SELECT * FROM SESSION WHERE sessionSubjectId = :subjectId")
    fun getRecentSessionForSubject(subjectId : Int) : Flow<List<Session>>

    @Query("SELECT SUM(duration) FROM SESSION ")
    fun   getTotalSessionDuration() : Flow<Long>

    @Query("SELECT SUM(duration) FROM SESSION WHERE sessionSubjectId = :subjectId")
    fun getTotalSessionDurationBySubjectId(subjectId: Int) : Flow<Long>

    @Query("DELETE FROM SESSION WHERE sessionSubjectId = :subjectId")
    fun deleteSessionBySubjectId(subjectId: Int)
}