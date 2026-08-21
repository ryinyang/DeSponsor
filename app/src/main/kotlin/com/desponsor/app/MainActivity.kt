package com.desponsor.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.desponsor.app.navigation.AppNavGraph
import com.desponsor.core.designsystem.DeSponsorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as DeSponsorApplication).container
        setContent {
            DeSponsorTheme {
                AppNavGraph(container)
            }
        }
    }
}
