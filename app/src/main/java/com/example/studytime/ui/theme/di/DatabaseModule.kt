package com.example.studytime.ui.theme.di

import android.app.Application
import androidx.room.Room
import com.example.studytime.ui.theme.data.local.local.AppDatabase
import com.example.studytime.ui.theme.data.local.local.SessionDao
import com.example.studytime.ui.theme.data.local.local.SubjectDao
import com.example.studytime.ui.theme.data.local.local.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        application: Application
    ) : AppDatabase{

        return Room
            .databaseBuilder(
                application,
                AppDatabase::class.java,
                "studysmart.db"
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideSubjectDao(database : AppDatabase) : SubjectDao {
        return database.subjectDao()
    }
    @Provides
    @Singleton
    fun provideSessionDao(database : AppDatabase) : SessionDao {
        return database.sessionDao()
    }
    @Provides
    @Singleton
    fun provideTaskDao(database : AppDatabase) : TaskDao {
        return database.taskDao()
    }
}