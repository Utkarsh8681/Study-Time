package com.example.studytime.ui.theme.Components

import android.app.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.studytime.ui.theme.Domain.model.Subject

@Composable
fun AddSubjectDialog(
    isOpen : Boolean,
    title : String = "Add/Update Subject",
    selectedColor : List<Color>,
    subjectName : String,
    goalHours : String,
    onSubjectNameChange : (String) -> Unit,
    onGoalHoursChange : (String) -> Unit,
//    onSelectChange : (List<Color>) -> Unit,
    onColorChange : (List<Color>) -> Unit,
    onDismissRequest : () -> Unit,
    onConfirmButtonClick : () -> Unit
) {
    var subjectNameError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var goalHoursError by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    subjectNameError = when {
        subjectName.isBlank()-> "Enter Subject Name"
        subjectName.length >20 -> "Subject Name Length Should be less than 20 word"
        subjectName.length<2 -> "Subject name should be greater than 2"
        else -> null
    }
    goalHoursError = when {
        goalHours.isBlank()-> "Enter Goal Hours Name"
        goalHours.toFloatOrNull() == null -> "Invalid Number"
        goalHours.toFloat() < 1f -> " Goal Hours should be greater than 1 Hour"
        else -> null
    }




if(isOpen) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = {

               Column {
                   Row (
                       modifier = Modifier.fillMaxWidth(),
                       horizontalArrangement = Arrangement.SpaceAround
                   ){
                       Subject.subjectCardColors.forEach{colors ->
                           Box (
                               modifier = Modifier
                                   .size(25.dp)
                                   .clip(CircleShape)
                                   .border(
                                       width = 1.dp, color = if (colors == selectedColor) {
                                           Color.Black
                                       } else Color.Transparent
                                   )
                                   .background(brush = Brush.verticalGradient(colors))
                                   .clickable { onColorChange(colors) }


                           )


                       }
                   }
                 OutlinedTextField(
                     value = subjectName,
                     onValueChange = onSubjectNameChange,
                     label = { Text(text = "Subject Name") },
                     singleLine = true ,
                     isError = subjectNameError != null && subjectName.isNotBlank(),
                     supportingText = { Text(text = subjectNameError.orEmpty())},
                     keyboardOptions = KeyboardOptions.Default
                 )
                   Spacer(modifier = Modifier.height(10.dp))
                 OutlinedTextField(
                     value = goalHours,
                     onValueChange = onGoalHoursChange,
                     label = { Text(text = "Goal Study Hours") },
                     singleLine = true,
                     isError = goalHoursError != null && goalHours.isNotBlank(),
                     supportingText = { Text(text = goalHoursError.orEmpty())},
                     keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                 )

               }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")

            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmButtonClick ,
                enabled = subjectNameError == null && goalHoursError == null
                )

            {

                Text(text = "Save")
            }
        }

    )
}
}

//@Preview
//@Composable
//private fun Dialog() {
// AddSubjectDialog(
////     isOpen = true,
////     selectedColor = ,
////     subjectName = ,
////     goalHours = ,
////     onSubjectNameChange = ,
////     onGoalHoursChange = ,
////     onColorChange = ,
////     onDismissRequest = { /*TODO*/ }) {
////
//// }
//
//}