package com.example.studytime.ui.theme.Components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDatePicker(
    state : DatePickerState,
    isOpen : Boolean,
    dismissButtonText : String = "Cancel",
    confirmButtonText : String = "OK",
    onDismissRequest : ()-> Unit,
    onConfirmClicked : ()-> Unit,

) {
    if (isOpen) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = { onConfirmClicked() }) {
                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = { onDismissRequest() }) {
                    Text(text = dismissButtonText)
                }
            }, content = {
                DatePicker(state = state ,
                    dateValidator = {timestamp ->
                        val selectedDate = Instant
                            .ofEpochMilli(timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        val currentDate = LocalDate.now(ZoneId.systemDefault())
                        selectedDate >=currentDate

                    }


                )
            }
        )
    }
}