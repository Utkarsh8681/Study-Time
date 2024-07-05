package com.example.studytime.ui.theme.Domain.model.repository


import com.example.studytime.ui.theme.Domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun upsertTask(task: Task)


    suspend fun deleteTask(taskId : Int)


    suspend fun deleteTaskBySubjectId(subjectId : Int)



    suspend fun getTaskById(taskId: Int): Task?


    fun getTaskForSubjects(subjectId: Int) : Flow<List<Task>>
    fun getUpcomingTaskForSubject(subjectId: Int) : Flow<List<Task>>
    fun getCompletedTaskForSubject(subjectId: Int) : Flow<List<Task>>


    fun getAllTasks(): Flow<List<Task>>
}