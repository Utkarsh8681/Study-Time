package com.example.studytime.ui.theme.task

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studytime.ui.theme.Components.DeleteSessionDialog
import com.example.studytime.ui.theme.Components.SubjectListBottomSheet
import com.example.studytime.ui.theme.Components.TaskCheckBox
import com.example.studytime.ui.theme.Components.TaskDatePicker
//import com.example.studytime.ui.theme.Components.subjects
import com.example.studytime.ui.theme.Util.Priority
import com.example.studytime.ui.theme.Util.SnackbarEvent
import com.example.studytime.ui.theme.Util.changeMillisTODateString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant

data class TaskScreenNavArgs(
    val taskId : Int?,
    val subjectId : Int?
)

@Destination(navArgsDelegate = TaskScreenNavArgs::class)
@Composable
fun TaskScreenRoute(
    navigator : DestinationsNavigator
) {
    val viewModel: TaskViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    TaskScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackbarEvent = viewModel.snackbarEventFlow,
        onBackIconClick = {
            navigator.navigateUp()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreen(
    state : TaskState,
    snackbarEvent : SharedFlow<SnackbarEvent>,
    onEvent : (TaskEvent) -> Unit,
    onBackIconClick: () -> Unit
) {


    val scope = rememberCoroutineScope()
//    var title by rememberSaveable {
//        mutableStateOf("")
//    }
//    var description by rememberSaveable {
//        mutableStateOf("")
//    }
    var isDatePickerOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var titleError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var descriptionError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var deleteTaskDialog by remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState()
    var isBottomSheetOpen by remember {
        mutableStateOf(false)
    }

    titleError = when{
        state.title.length < 4 -> "Title too small"
        state.title.length > 20 -> "Title too large"
        state.title.isBlank() -> "Enter Title"

        else -> null
    }
    descriptionError = when{
        state.description.length < 5 -> "Title too small"
        state.description.length > 200 -> "Title too small"
        state.description.isBlank() -> "Enter Description"

        else -> null
    }
    val snackbarHostState  = remember {
        SnackbarHostState()
    }
    LaunchedEffect(key1 = true) {
        snackbarEvent.collectLatest {event ->
            when(event){
                is SnackbarEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )
                }

                SnackbarEvent.NavigateUp -> {onBackIconClick()}
            }
        }
    }
    DeleteSessionDialog(isOpen = deleteTaskDialog   ,
        title = "Delete Subject",
        bodyText = "Are you Sure you want to delete this session? your studied hours will be reduced by " +
                "by this session time. this action cannot be undo.",
        onDismissRequest = { deleteTaskDialog = false },
        onConfirmButtonClick = {
            onEvent(TaskEvent.DeleteTask)
            deleteTaskDialog = false}
    )

    TaskDatePicker(
        state = datePickerState,
        isOpen =isDatePickerOpen,
        onDismissRequest = {
            isDatePickerOpen = false },
      onConfirmClicked = {
          onEvent(TaskEvent.onDateChange(millis = datePickerState.selectedDateMillis))
          isDatePickerOpen = false}
    )

    SubjectListBottomSheet(
        sheetState =sheetState,
        isOpen = isBottomSheetOpen,
        subjectsList = state.subjects,
        onDismissRequest = { isBottomSheetOpen = false },
        onSubjectClicked = { subject ->
                           scope.launch { sheetState.hide() }.invokeOnCompletion {
                               if(!sheetState.isVisible) isBottomSheetOpen = false
                           }
            onEvent(TaskEvent.onRelatedSubjectSelect(subject))
        }
    )


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        topBar = {
            TaskScreenTopBar(
                title = "Tasks",
                isTaskExist = state.currentTaskId != null,
                isComplete = state.isTaskComplete ,
                checkBoxBorderColor = state.priority.color,
                onBackIconClick = { onBackIconClick() },
                onCheckBoxClick = {onEvent(TaskEvent.OnIsCompleteChange)},
                onDeleteIconClick = { deleteTaskDialog = true })


        }
    ){paddingValues ->
        Column (
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { onEvent(TaskEvent.OnTitleChange(it))},
                singleLine = true,
                label = { Text(text = "Title")},
                modifier = Modifier.fillMaxWidth(),
                isError = titleError != null && state.title.isNotBlank(),
                supportingText = { Text(text = titleError.orEmpty())}
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = state.description,
                onValueChange = {onEvent(TaskEvent.OnDescriptionChange(it))},
                label = { Text(text = "Description")},
                modifier = Modifier.fillMaxWidth(),
                isError = descriptionError != null && state.description.isNotBlank(),
                supportingText = { Text(text = descriptionError.orEmpty())}
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Due Date" ,
                style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = state.dueDate.changeMillisTODateString() , style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = { isDatePickerOpen = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription ="Set Date" )
                }

            }
            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Priority" ,style = MaterialTheme.typography.bodySmall , fontWeight = FontWeight.Bold )
Spacer(modifier = Modifier.height(10.dp))

            Row (modifier = Modifier.fillMaxWidth() ,
horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
                ){

                Priority.entries.forEach { priority ->
                    PriorityCard(
                        modifier = Modifier.weight(1f),
                        label =priority.title ,
                        backgroundColor = priority.color,
                        borderColor = if (priority == state.priority) {
                       Color.White
                        } else {Color.Transparent},
                        onCLick = {onEvent(TaskEvent.onPriortyChange(priority)) },
                        labelColor =if(priority == state.priority) Color.White
                        else Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))



            Text(text = "Related to subject" ,style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold )
            Spacer(modifier = Modifier.height(10.dp))
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                val firstSubject = state.subjects.firstOrNull()?.name?:""
                Text(text =state.relatedToSubject?:firstSubject , style = MaterialTheme.typography.bodyMedium)
                IconButton(onClick = {isBottomSheetOpen = true }) {

                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription ="Select Subject")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Button(
               enabled = titleError == null,
                onClick = { onEvent(TaskEvent.SaveTask) } , modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp))
            {
                Text(text = "Save")
            }

            
        }

    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopBar(
    title : String,
    isTaskExist : Boolean,
    isComplete : Boolean,
    checkBoxBorderColor : Color,
    onBackIconClick : ()-> Unit,
    onDeleteIconClick : () -> Unit,
    onCheckBoxClick : ()-> Unit 
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { onBackIconClick()}) {
                Icon(imageVector =Icons.Default.ArrowBack , contentDescription ="Back Button")
            }
        } ,
        title = {Text(text = title)} ,

        actions = {
            if(isTaskExist){
                TaskCheckBox(isComplete = isComplete, borderColor =checkBoxBorderColor , onCheckBoxClick = onCheckBoxClick )

            }

            IconButton(onClick = { onDeleteIconClick() }

            ){
                Icon(imageVector =Icons.Default.Delete , contentDescription ="Back Button" )
            }

        }
    )
}

@Composable
fun PriorityCard(
    modifier: Modifier,
    backgroundColor : Color,
    borderColor : Color,
    onCLick :()-> Unit,
    label : String,
    labelColor : Color
) {
    Box (
        modifier = modifier
            .background(backgroundColor)
            .clickable { onCLick() }
            .padding(10.dp)
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .padding(5.dp),
        contentAlignment = Alignment.Center
    ){
         Text(text = label , color = labelColor)
    }
}

