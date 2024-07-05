package com.example.studytime.ui.theme.Domain.model.repository


import com.example.studytime.ui.theme.Domain.model.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    suspend fun upsertSubject(subject: Subject)


    fun getTotalSubjectCount(): Flow<Int>


    fun getTotalGoalHours(): Flow<Float>


    suspend fun getSubjectById(subjectId: Int ) : Subject?


    suspend fun deleteSubject(subjectId: Int)


    fun getAllSubjects() : Flow<List<Subject>>
}