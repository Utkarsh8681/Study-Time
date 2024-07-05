package com.example.studytime.ui.theme.Domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    val title : String,
    val taskSubjectId : Int,
    val description : String,
    val dueDate : Long,
    val priority : Int,
    val relatedSubject : String,
    val isComplete : Boolean,
    @PrimaryKey
    val taskId : Int,
//    val subjectId : Int
)
