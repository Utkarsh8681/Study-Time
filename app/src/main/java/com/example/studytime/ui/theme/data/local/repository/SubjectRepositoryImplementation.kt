package com.example.studytime.ui.theme.data.local.repository

import com.example.studytime.ui.theme.Domain.model.Subject
import com.example.studytime.ui.theme.Domain.model.repository.SubjectRepository
import com.example.studytime.ui.theme.data.local.local.SessionDao
import com.example.studytime.ui.theme.data.local.local.SubjectDao
import com.example.studytime.ui.theme.data.local.local.TaskDao
import kotlinx.coroutines.flow.Flow
import java.security.PrivateKey
import javax.inject.Inject

class SubjectRepositoryImplementation @Inject constructor(
    private val subjectDao: SubjectDao,
    private val taskDao : TaskDao,
    private val sessionDao : SessionDao
) : SubjectRepository {
    override suspend fun upsertSubject(subject: Subject) {
       subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int> {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float> {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun getSubjectById(subjectId : Int): Subject? {
        return subjectDao.getSubjectById(subjectId)
    }

    override suspend fun deleteSubject(subjectId: Int) {
        taskDao.deleteTaskBySubjectId(subjectId)
        sessionDao.deleteSessionBySubjectId(subjectId)
        subjectDao.deleteSubject(subjectId)
    }


    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }

}