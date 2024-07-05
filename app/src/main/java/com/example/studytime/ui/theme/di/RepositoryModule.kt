package com.example.studytime.ui.theme.di

import com.example.studytime.ui.theme.Domain.model.repository.SessionRepository
import com.example.studytime.ui.theme.Domain.model.repository.SubjectRepository
import com.example.studytime.ui.theme.Domain.model.repository.TaskRepository
import com.example.studytime.ui.theme.data.local.repository.SessionRepositoryImplementation
import com.example.studytime.ui.theme.data.local.repository.SubjectRepositoryImplementation
import com.example.studytime.ui.theme.data.local.repository.TaskRepositoryImplementation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
 abstract class RepositoryModule {

@Singleton
@Binds
     abstract fun bindSubjectRepository(
         impl : SubjectRepositoryImplementation
     ) : SubjectRepository


     @Singleton
     @Binds
     abstract fun bindSessionRepository(
         impl : SessionRepositoryImplementation
     ) : SessionRepository


     @Singleton
     @Binds
     abstract fun bindTaskRepository(
         impl : TaskRepositoryImplementation
     ) : TaskRepository
}