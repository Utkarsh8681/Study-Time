package com.example.studytime.ui.theme.data.local.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.Subject
import com.example.studytime.ui.theme.Domain.model.Task

@Database(
    entities = [Subject :: class , Session :: class , Task :: class],
    version = 1
)

@TypeConverters(ColorListConverter ::class)
abstract class AppDatabase : RoomDatabase() {

abstract fun sessionDao() : SessionDao
abstract fun taskDao() : TaskDao
abstract fun subjectDao() : SubjectDao

}