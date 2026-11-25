package com.navyblue.navyweather.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.navyblue.navyweather.shared.App

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Draw behind the system bars; ComponentActivity's version also picks legible
        // status/navigation bar icon colors for light and dark themes.
        enableEdgeToEdge()
        setContent { App() }
    }
}
