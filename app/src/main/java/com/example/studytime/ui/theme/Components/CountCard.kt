package com.example.studytime.ui.theme.Components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CountCard(
    modifier: Modifier  = Modifier ,
    count : String ,
    headlineText : String
    ) {
    ElevatedCard (modifier = modifier){
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Text(text = headlineText,
                style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = count,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 30.sp))
        }
       
        
    }
}

@Preview
@Composable
private fun Carder() {
    CountCard(count = "14", headlineText = "Hello World")
}