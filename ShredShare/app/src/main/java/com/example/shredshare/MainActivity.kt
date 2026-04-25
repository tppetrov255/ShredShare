package com.example.shredshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.shredshare.navigation.AppNavGraph
import com.example.shredshare.ui.theme.themes.ShredShareTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShredShareTheme {
                AppNavGraph()
            }
        }
    }
}