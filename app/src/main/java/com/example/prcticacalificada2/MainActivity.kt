package com.example.prcticacalificada2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.prcticacalificada2.ui.navigation.AppNavigation
import com.example.prcticacalificada2.ui.theme.PRÁCTICACALIFICADA2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PRÁCTICACALIFICADA2Theme {
                AppNavigation()
            }
        }
    }
}
