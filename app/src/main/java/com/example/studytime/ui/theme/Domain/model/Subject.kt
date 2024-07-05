package com.example.studytime.ui.theme.Domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.studytime.ui.theme.gradient1
import com.example.studytime.ui.theme.gradient2
import com.example.studytime.ui.theme.gradient3
import com.example.studytime.ui.theme.gradient4
import com.example.studytime.ui.theme.gradient5
@Entity
data class Subject(
    val name: String,
    val goalHours: Float,
    val colors: List<Int>,
    @PrimaryKey(autoGenerate = true)
    val subjectId : Int? = null
){
    companion object{
        val subjectCardColors  = listOf(gradient1 , gradient2 , gradient3, gradient4, gradient5)
    }
}
