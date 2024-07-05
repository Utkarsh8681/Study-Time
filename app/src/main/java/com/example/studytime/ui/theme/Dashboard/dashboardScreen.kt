package com.example.studytime.ui.theme.Dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studytime.R
import com.example.studytime.ui.theme.Components.AddSubjectDialog
import com.example.studytime.ui.theme.Components.CountCard
import com.example.studytime.ui.theme.Components.DeleteSessionDialog
import com.example.studytime.ui.theme.Components.SubjectCard
import com.example.studytime.ui.theme.Components.tasksList
//import com.example.studytime.ui.theme.Dashboard.destinations.SessionScreenRouteDestination
//import com.example.studytime.ui.theme.Dashboard.destinations.SubjectScreenRouteDestination
//import com.example.studytime.ui.theme.Dashboard.destinations.TaskScreenRouteDestination
//import com.example.studytime.ui.theme.Dashboard.destinations.SessionScreenRouteDestination
//import com.example.studytime.ui.theme.Dashboard.destinations.SubjectScreenRouteDestination
//import com.example.studytime.ui.theme.Dashboard.destinations.TaskScreenRouteDestination
import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.Subject
import com.example.studytime.ui.theme.Domain.model.Task
import com.example.studytime.ui.theme.Util.SnackbarEvent
import com.example.studytime.ui.theme.dash.DashboardEvent
import com.example.studytime.ui.theme.dash.DashboardState
import com.example.studytime.ui.theme.dash.DashboardViewModel
import com.example.studytime.ui.theme.destinations.SessionScreenRouteDestination
import com.example.studytime.ui.theme.destinations.SubjectScreenRouteDestination
import com.example.studytime.ui.theme.destinations.TaskScreenRouteDestination
import com.example.studytime.ui.theme.task.TaskScreenNavArgs
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import studySessionList
@RootNavGraph(start = true)
@Destination
@Composable
fun DashboardScreenRoute(
    navigator : DestinationsNavigator
) {
    val viewModel : DashboardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val task by viewModel.tasks.collectAsStateWithLifecycle()
    val recentSession by viewModel.recentSessions.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        task = task,
        recentSession = recentSession,
        onEvent = viewModel::onEvent,
        onSubjectCardClick ={subjectId ->
                            subjectId?.let {
                                val navArgs = SubjectScreenNavArgs(subjectId = subjectId)
                                navigator.navigate(SubjectScreenRouteDestination(navArgs = navArgs))
                            }


        },
        onTaskCardClicked ={taskId ->
            val navArgs = TaskScreenNavArgs(taskId = taskId , subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArgs))

        },
        onStudySessionClicked = {
navigator.navigate(SessionScreenRouteDestination())
        },
        snackbarEvent = viewModel.snackbarEventFlow
    )
}
@Composable
private fun DashboardScreen(
    state : DashboardState,
    task : List<Task>,
    recentSession : List<Session>,
    onEvent :(DashboardEvent) -> Unit,
    snackbarEvent : SharedFlow<SnackbarEvent>,
    onSubjectCardClick :(Int?)->Unit,
    onStudySessionClicked :()-> Unit,
    onTaskCardClicked : (Int?) -> Unit
) {



    var addSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var deleteSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
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

                SnackbarEvent.NavigateUp -> {}
            }
        }
    }

    AddSubjectDialog(
        isOpen = addSubjectDialogOpen,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange = {onEvent(DashboardEvent.OnSubjectNameChange(it))},
        onGoalHoursChange = {onEvent(DashboardEvent.OnGoalStudyHoursChange(it))},
        selectedColor = state.subjectCardColors,
        onColorChange = {onEvent(DashboardEvent.OnSubjectCardColorChange(it))},
        onDismissRequest = { addSubjectDialogOpen = false } ,
        onConfirmButtonClick = {
            onEvent(DashboardEvent.SaveSubject)
            addSubjectDialogOpen =false}
    )

    DeleteSessionDialog(isOpen = deleteSubjectDialogOpen   ,
        title = "Delete Subject",
        bodyText = "Are you Sure you want to delete this session? your studied hours will be reduced by " +
        "by this session time. this action cannot be undo.",
        onDismissRequest = { deleteSubjectDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(DashboardEvent.DeleteSession)
            deleteSubjectDialogOpen = false}
        )


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState)},
        topBar = {DashboardScreenTopAppBar()}
    ) {paddingValues ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
item {
    CountCardSection(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        subjectCount =state.totalSubjectCount,
        studiedHours =state.totalStudiedHours.toString(),
        goalHours =state.totalGoalStudiedHours.toString()
    )
}
            item {
                SubjectsCardSection(modifier = Modifier.fillMaxWidth(), subjectList = state.subjects , onAddIconClicked = {addSubjectDialogOpen = true} , onSubjectCardClick = onSubjectCardClick)
            }
            item { 
                Button(onClick = { onStudySessionClicked() },
                    modifier = Modifier
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                        .fillMaxWidth()

                    ) {
                    Text(text = "Start Study Session" )
                }
            }
          tasksList(
              sectionTitle = "Upcoming Tasks",
              emptyLis = "You don't have any upcomonh tasks \n " +
                      "Click the + button in Subject screen to add a task. ",
              tasks = task,
              onTaskCardClicked = onTaskCardClicked,
              onCheckBoxClicked = {onEvent(DashboardEvent.OnTaskIsCompleteButtonClick(it))}
          )
            studySessionList(
                emptyLis = "You don't have any upcomonh tasks \n " +
                        "Click the + button in Subject screen to add a task. ",
                sectionTitle = "Recent Study Sessions" ,
                sessions = recentSession,
                onDeleteIconClick = {
                    onEvent(DashboardEvent.OnDeleteSessionButtonClick(it))
                    deleteSubjectDialogOpen = true}

            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenTopAppBar() {
    CenterAlignedTopAppBar(title = {
Text(text = "StudySmart" , style = MaterialTheme.typography.headlineMedium)
    })
}

@Composable
private fun CountCardSection(
    modifier: Modifier,
    subjectCount : Int,
    studiedHours : String,
    goalHours : String
) {
    Row (modifier = modifier){
        CountCard(
            modifier = Modifier.weight(1f),
            headlineText = "Subject Count",
            count = "$subjectCount"
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(modifier = Modifier.weight(1f),
            count = studiedHours,
            headlineText = "Study Hours")
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(modifier = Modifier.weight(1f),
            headlineText = "Goal Study Hours",
            count = goalHours
        )
    }
}

@Composable
fun SubjectsCardSection(
    modifier: Modifier,
    subjectList : List<Subject>,
    emptyList : String =  "There are no subject. \n Click + to add subjects.",
    onAddIconClicked : () -> Unit ,
    onSubjectCardClick: (Int?) -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth() ,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
            ) {
            Text(
                text = "Subjects",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            IconButton(onClick = { onAddIconClicked() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add button")
            }
        }
        if(subjectList.isEmpty()){
            Image(
                modifier = modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.book),
                contentDescription = emptyList
            )
            Text(
                modifier  =Modifier.fillMaxWidth(),
                text = emptyList,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

        }
       LazyRow (
           horizontalArrangement = Arrangement.spacedBy(12.dp),
           contentPadding = PaddingValues(start = 12.dp , end = 12.dp)
       ){
           items(subjectList){subject ->
              SubjectCard(subjectName = subject.name, gradientColors =subject.colors.map { Color(it) }, onClick = {onSubjectCardClick(subject.subjectId)})

           }
       }
    }
}

