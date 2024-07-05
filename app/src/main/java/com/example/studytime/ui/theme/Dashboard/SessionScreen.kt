package com.example.studytime.ui.theme.Dashboard
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studytime.ui.theme.Components.DeleteSessionDialog
import com.example.studytime.ui.theme.Components.SubjectListBottomSheet
import com.example.studytime.ui.theme.Red
import com.example.studytime.ui.theme.Util.Constants.Action_Service_START
import com.example.studytime.ui.theme.Util.Constants.Action_Service_STOP
import com.example.studytime.ui.theme.Util.ServiceHelper
import com.example.studytime.ui.theme.Util.SnackbarEvent
import com.example.studytime.ui.theme.session.SessionEvent
import com.example.studytime.ui.theme.session.SessionState
import com.example.studytime.ui.theme.session.StudySessionTimerService
import com.example.studytime.ui.theme.session.TimerState
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import studySessionList
import kotlin.time.DurationUnit

@Destination(
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            uriPattern = "study_Time://dashboard/session"
        )
    ]
)

@Composable
fun SessionScreenRoute(
    navigator : DestinationsNavigator,
    timerService : StudySessionTimerService
) {
    val viewModel : SessionViewModel = hiltViewModel()
val state by viewModel.state.collectAsStateWithLifecycle()
    SessionScreen(
        onBackButtonClick = { navigator.navigateUp() },
        state =state,
        snackbarEvent = viewModel.snackbarEventFlow,
        onEvent = viewModel::onEvent,
        timerService = timerService
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreen(
    state : SessionState,
    snackbarEvent : SharedFlow<SnackbarEvent>,
    onEvent : (SessionEvent)-> Unit,
//    onStudySessionClicked : ()->Unit
    onBackButtonClick: () -> Unit,
    timerService : StudySessionTimerService
) {

    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val currentTimerState by timerService.currentTimerState

    val context = LocalContext.current
    var isBottomSheetOpen by remember {
        mutableStateOf(false)
    }
//    var deleteSessionDialogOpen by remember {
//        mutableStateOf(false)
//    }

    var deleteSubjectDialogOpen by remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()


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
    LaunchedEffect(key1 = state.subjects ) {
        val subjectId = timerService.subjectId.value
        onEvent(
            SessionEvent.UpdateSubjectIdAndRelatedSubject(
                subjectId = subjectId,
                relatedSubject = state.subjects.find { it.subjectId == subjectId }?.name
            )
        )
    }


    SubjectListBottomSheet(
        sheetState =sheetState,
        isOpen = isBottomSheetOpen,
        subjectsList = state.subjects,
        onSubjectClicked = { subject ->
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if(!sheetState.isVisible) isBottomSheetOpen = false
            }
            onEvent(SessionEvent.onRelatedSubjectChange(subject))
        },
        onDismissRequest = { isBottomSheetOpen = false }
    )

    DeleteSessionDialog(isOpen = deleteSubjectDialogOpen   ,
        title = "Delete Session",
        bodyText = "Are you Sure you want to delete this session? your studied hours will be reduced by " +
                "by this session time. this action cannot be undo.",
        onDismissRequest = { deleteSubjectDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(SessionEvent.deleteSession)
            deleteSubjectDialogOpen = false}
    )
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            SessionScreenTopAppBar(title = "Study Session" , onBackButtonClick = onBackButtonClick)
        }
    ) {paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(12.dp)
                .fillMaxSize()
        ) {

            item {
                    TimerSection(modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                        hours = hours,
                        minutes = minutes,
                        seconds = seconds
                        )
            }
            item {
                RelatedToSubjectSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    onClickDropDown = { isBottomSheetOpen = true },
                    relatedToSubject = state.relatedToSubject ?:"",
                    seconds = seconds
                )
            }
            item{
                ButtonSection(
                    modifier = Modifier.fillMaxWidth(),
                    startButtonClick = {
                        if(state.subjectId != null && state.relatedToSubject != null) {
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action =
                                if (currentTimerState == TimerState.STARTED) {
                                    Action_Service_STOP
                                } else
                                    Action_Service_START
                            )
                            timerService.subjectId.value = state.subjectId
                        }else{
                            onEvent(SessionEvent.NotifyToUpdateSubject)
                        }
                    },
                    finishButtonClick = {
                        val duration = timerService.duration.toLong(DurationUnit.SECONDS)
                        if(duration >= 36) {
                            ServiceHelper.triggerForegroundService(
                                context = context,
                                action = Action_Service_START
                            )
                        }
                        onEvent(SessionEvent.saveSession(duration))
                                        } ,
                    cancelButtonClick = {
                        ServiceHelper.triggerForegroundService(
                        context =context ,
                        action =Action_Service_START)},
                    timerState = currentTimerState,
                    seconds= seconds
                    )
            }
            studySessionList(
                emptyLis = "You don't have any upcomonh tasks \n " +
                        "Click the + button in Subject screen to add a task. ",
                sectionTitle = "Study Sessions History" ,
                sessions = state.sessions,
                onDeleteIconClick = {sessions ->
                    onEvent(SessionEvent.onDeleteSessionButtinClick(sessions))
                    deleteSubjectDialogOpen = true}
            )

        }


    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreenTopAppBar(
    title : String,
    onBackButtonClick : () -> Unit
) {
    TopAppBar(
        title = {
                Text(text = title , style = MaterialTheme.typography.headlineSmall)
        },
        navigationIcon = {
            IconButton(onClick = { onBackButtonClick() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back Button")
            }
        }

    )
}

@Composable
fun TimerSection(
    modifier: Modifier,
    hours : String,
    minutes : String,
    seconds : String
) {
    Box (
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Box (
            modifier = Modifier
                .size(250.dp)
                .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape),
//            contentAlignment = Alignment.Center
        )
            Row {
                AnimatedContent(
                    targetState = hours,
                    transitionSpec = { timerTextAnimation() },
                    label = hours
                ) { hours ->
                    Text(
                        text = "$hours:",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                    )
                }
                AnimatedContent(
                    targetState = minutes,
                    transitionSpec = { timerTextAnimation() },
                    label = minutes
                ) { minutes ->
                    Text(
                        text = "$minutes:",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                    )
                }
                AnimatedContent(
                    targetState = seconds,
                    transitionSpec = { timerTextAnimation() },
                    label = seconds
                ) { seconds ->
                    Text(
                        text = seconds,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                    )
                }

        }
    }

}

@Composable
fun RelatedToSubjectSection(
    modifier: Modifier,
    onClickDropDown : () -> Unit,
    relatedToSubject : String,
    seconds : String
) {
    Column (
        modifier = modifier
    ){
        Text(
            text = "Related to subject",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
//        Spacer(modifier = modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = relatedToSubject, style = MaterialTheme.typography.bodyMedium)
            IconButton(onClick = {onClickDropDown() } , enabled = seconds == "00") {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Subject"
                )
            }
        }
    }
}

@Composable
private fun ButtonSection(
    modifier: Modifier,
    startButtonClick : ()-> Unit,
    finishButtonClick : ()-> Unit,
    cancelButtonClick : ()-> Unit,
    timerState : TimerState,
    seconds : String
) {
    Row (modifier = modifier
        .fillMaxWidth(1f)
        .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
        ){
        Button(onClick =  cancelButtonClick,
            enabled = seconds != "00" && timerState != TimerState.STARTED,
            modifier = Modifier.padding(horizontal = 7.dp , vertical = 5.dp) ) {
            Text(text = "Cancel")
        }
//        Spacer(modifier = )
        Button(
            onClick = startButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (timerState == TimerState.STARTED) Red
                else MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ),
            modifier = Modifier.padding(horizontal = 7.dp , vertical = 5.dp)) {
            Text(
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                maxLines = 1,
                text = when(timerState){
                    TimerState.STARTED -> "Stop"
                    TimerState.STOPPED -> "Resume"
                    else -> "Start"
                }
            )
        }
        Button(onClick = finishButtonClick,
            enabled = seconds != "00" && timerState != TimerState.STARTED,
            modifier = Modifier.padding(horizontal = 10.dp , vertical = 5.dp)) {
            Text(text = "Finish")
        }
    }


}



private fun timerTextAnimation(duration : Int = 600) : ContentTransform{
    return slideInVertically (animationSpec = tween(duration)){fullHeight ->  fullHeight}+
            fadeIn(animationSpec = tween(duration)) togetherWith
            slideOutVertically(animationSpec = tween(duration)){fullHeight ->  fullHeight}+
            fadeOut(animationSpec = tween(duration))
}


