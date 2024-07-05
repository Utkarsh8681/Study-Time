package com.example.studytime

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.example.studytime.ui.theme.Domain.model.Session
import com.example.studytime.ui.theme.Domain.model.Subject
import com.example.studytime.ui.theme.Domain.model.Task
import com.example.studytime.ui.theme.NavGraphs
import com.example.studytime.ui.theme.StudyTimeTheme
import com.example.studytime.ui.theme.destinations.SessionScreenRouteDestination
import com.example.studytime.ui.theme.session.StudySessionTimerService
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var isBound by mutableStateOf(false)
    private lateinit var timerService : StudySessionTimerService

    private val connection = object : ServiceConnection{
        override fun onServiceConnected(p0 : ComponentName?, service : IBinder?) {
            val binder=  service as StudySessionTimerService.StudySessionTimerBinder
            timerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(p0 : ComponentName?) {
           isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, StudySessionTimerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if (isBound){
                StudyTimeTheme {
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        dependenciesContainerBuilder = {
                            dependency(SessionScreenRouteDestination){timerService}
                        }
                    )

                }
            }

            requestPermission()
        }
    }
    private fun requestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        isBound = false
    }
}
val subjects = listOf(
    Subject(name = "Maths" , goalHours = 10f, colors = Subject.subjectCardColors[0].map { it.toArgb() } , subjectId = 1),
    Subject(name = "English" , goalHours = 10f, colors = Subject.subjectCardColors[1].map { it.toArgb() },subjectId = 2),
    Subject(name = "Physics" , goalHours = 10f, colors = Subject.subjectCardColors[2].map { it.toArgb() },subjectId = 3),
    Subject(name = "Maths" , goalHours = 10f, colors = Subject.subjectCardColors[3].map { it.toArgb() },subjectId = 4),
    Subject(name = "Maths" , goalHours = 10f, colors = Subject.subjectCardColors[4].map { it.toArgb() },subjectId = 5)
)

val sesso = listOf(
    Session(
        sessionSubjectId =0,
        sessionId = 0,
        relatedToSubject = "Hindi",
        duration = 2,
        date = 0L
    ),
    Session(
        sessionSubjectId =0,
        sessionId = 0,
        relatedToSubject = "Maths",
        duration = 2,
        date = 0L
    ),
    Session(
        sessionSubjectId =0,
        sessionId = 0,
        relatedToSubject = "Science",
        duration = 2,
        date = 0L
    ),
    Session(
        sessionSubjectId =0,
        sessionId = 0,
        relatedToSubject = "English",
        duration = 2,
        date = 0L
    ),
)

val tasks = listOf(
    Task(
        title = "Prepare Notes",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedSubject = "maths",
        isComplete = false,
        taskId = 1,
        taskSubjectId = 0
    ),
    Task(
        title = "Do Home Work",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedSubject = "maths",
        isComplete = false,
        taskId = 1,
        taskSubjectId = 0
    ),
    Task(
        title = "Go Coaching",
        description = "",
        dueDate = 0L,
        priority = 3,
        relatedSubject = "maths",
        isComplete = false,
        taskId = 1,
        taskSubjectId = 0
    ),
    Task(
        title = "Assignment",
        description = "",
        dueDate = 0L,
        priority = 4,
        relatedSubject = "maths",
        isComplete = false,
        taskId = 1,
        taskSubjectId = 0
    ),

    )