package com.famessa.deli_antojito

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.famessa.deli_antojito.ui.theme.Deli_antojitoTheme
import com.famessa.deli_antojito.views.LoginView
import com.famessa.deli_antojito.viewModels.LoginViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: LoginViewModel by viewModels()
        setContent {
            Deli_antojitoTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    LoginView(viewModel)
                }
            }
        }
    }
}
