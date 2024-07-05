package com.example.studytime.ui.theme.Util

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import com.example.studytime.ui.theme.Green
import com.example.studytime.ui.theme.Orange
import com.example.studytime.ui.theme.Red
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Priority(
    val title : String,
    val color : Color,
    val value : Int
) {
    Low(title = "low" , color = Green , value = 0),
    Medium(title = "medium" , color = Orange , value = 1),
    High(title = "High" , color = Red , value = 2);

    companion object{
        fun fromInt(value: Int) = values().firstOrNull(){
            it.value == value
        } ?: Medium
    }
}
fun Long?.changeMillisTODateString(): String {
    val date : LocalDate = this?.let{
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yy"))
}

fun Long.toHours() : Float{
    val hours = this.toFloat()/3600f
    return "%.2f".format(hours).toFloat()
}

sealed class SnackbarEvent{
    data class ShowSnackbar(
        val message :String,
        val duration : SnackbarDuration= SnackbarDuration.Short) : SnackbarEvent()

    data object NavigateUp : SnackbarEvent()

}
fun Int.pad():String{
    return this.toString().padStart(length = 2 , padChar = '0')
}