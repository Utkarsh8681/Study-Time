package com.example.studytime.ui.theme.task

import com.example.studytime.ui.theme.Domain.model.Subject
import com.example.studytime.ui.theme.Util.Priority

sealed class TaskEvent {

    data class OnTitleChange(val title : String) : TaskEvent()

    data class OnDescriptionChange(val description : String): TaskEvent()

    data class onDateChange(val millis : Long?) : TaskEvent()

    data class onPriortyChange(val priority : Priority) : TaskEvent()

    data class onRelatedSubjectSelect(val subject : Subject) : TaskEvent()

    data object OnIsCompleteChange : TaskEvent()

    data object SaveTask : TaskEvent()

    data object DeleteTask : TaskEvent()


}