package com.example.studytime.ui.theme.Util

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.example.studytime.MainActivity
import com.example.studytime.ui.theme.Util.Constants.CLICK_REQUEST_CODE
import com.example.studytime.ui.theme.session.StudySessionTimerService
import dagger.hilt.android.internal.Contexts

object ServiceHelper {

    fun clickPendingIntent(context : Context):PendingIntent{
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "study_Time://dashboard/session".toUri(),
            context,
            MainActivity::class.java
        )
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                CLICK_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE

            )
        }
    }

    fun triggerForegroundService(context : Context, action: String){
        Intent(context, StudySessionTimerService::class.java).apply {
            this.action = action
            context.startService(this)
        }
    }
}