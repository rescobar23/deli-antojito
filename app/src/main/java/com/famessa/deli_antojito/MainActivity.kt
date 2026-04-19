package com.famessa.deli_antojito

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.famessa.deli_antojito.core.ui.theme.Deli_antojitoTheme
import com.famessa.deli_antojito.data.db.AppDatabase
import com.famessa.deli_antojito.data.repository.ProductoRepositoryImpl
import com.famessa.deli_antojito.feature.home.HomeView
import com.famessa.deli_antojito.feature.home.HomeViewModel
import com.famessa.deli_antojito.feature.home.HomeViewModelFactory
import com.famessa.feature.products.ProductsAdminActivity

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear instancia de la base de datos
        val database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "deli_antojito_db"
        ).build()

        // Crear repositorio usando la implementación de data
        val repository = ProductoRepositoryImpl(database)

        setContent {
            Deli_antojitoTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
                    Box(modifier = Modifier.padding(innerPadding)) {
                        HomeView(
                            onAdminClick = {
                                val intent = Intent(this@MainActivity, ProductsAdminActivity::class.java)
                                startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}
