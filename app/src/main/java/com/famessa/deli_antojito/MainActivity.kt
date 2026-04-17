package com.famessa.deli_antojito

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.famessa.deli_antojito.ui.theme.Deli_antojitoTheme
import com.famessa.deli_antojito.views.HomeView
import kotlin.getValue

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deli_antojitoTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    HomeView()
                }
            }
        }
    }
}

@Preview
@Composable
fun ApplicationPreview() {
    Deli_antojitoTheme {
        HomeView()
    }
}
