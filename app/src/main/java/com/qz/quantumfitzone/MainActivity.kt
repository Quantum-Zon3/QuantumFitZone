package com.qz.quantumfitzone

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.qz.quantumfitzone.ui.navigation.NavGraph
import com.qz.quantumfitzone.ui.theme.QuantumFitZoneTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuantumFitZoneTheme {
                NavGraph()
            }
        }
    }
}

