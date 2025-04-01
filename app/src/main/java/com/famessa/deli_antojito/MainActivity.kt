package com.famessa.deli_antojito

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.famessa.deli_antojito.ui.theme.AquaLv5
import com.famessa.deli_antojito.ui.theme.Deli_antojitoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Deli_antojitoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainContent(innerPadding)
                }
            }
        }
    }
}

@Composable
fun MainContent(innerPadding: PaddingValues) {

    val context = LocalContext.current
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ){
        Spacer(modifier=Modifier.weight(1f))
        Text(
            text = "Usuario",
            modifier = Modifier.fillMaxWidth().padding(2.dp)
        )
        BasicTextField(
            state = TextFieldState(),
                    modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Contraseña",
            modifier = Modifier.fillMaxWidth().padding(2.dp)
        )
        BasicTextField(
            state = TextFieldState(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier=Modifier.weight(1f))
        Button(
            onClick = {
                Toast.makeText(context, "Iniciando Sesion", Toast.LENGTH_LONG).show()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview(){
    MainContent(PaddingValues(2.dp))
}