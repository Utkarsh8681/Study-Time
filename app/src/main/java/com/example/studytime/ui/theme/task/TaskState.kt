package com.example.studytime.ui.theme.task

import com.example.studytime.ui.theme.Domain.model.Subject
import com.example.studytime.ui.theme.Util.Priority

data class TaskState(
    val title : String = "",
    val description: String = "",
    val dueDate : Long? = null,
    val isTaskComplete : Boolean = false,
    val priority : Priority = Priority.Low,
    val relatedToSubject : String? = null,
    val subjects : List<Subject> = emptyList(),
    val subjectId : Int? = null,
    val currentTaskId : Int? = null
)
