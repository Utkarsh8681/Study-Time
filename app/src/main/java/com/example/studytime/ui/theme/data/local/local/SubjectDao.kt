package com.example.studytime.ui.theme.data.local.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.studytime.ui.theme.Domain.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Upsert
   suspend fun upsertSubject(subject: Subject)

@Query("Select COUNT(*) FROM Subject")
    fun getTotalSubjectCount(): Flow<Int>

    @Query("SELECT SUM(goalHours) FROM Subject ")
    fun getTotalGoalHours():Flow<Float>

    @Query("SELECT *From Subject WHERE subjectId = :subjectId")
    suspend fun getSubjectById(subjectId: Int ) : Subject?

    @Query("DELETE From Subject WHERE subjectId = :subjectId")
    suspend fun deleteSubject(subjectId: Int)

    @Query("SELECT *From Subject" )
    fun getAllSubjects() : Flow<List<Subject>>
}