package com.famessa.deli_antojito

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.famessa.deli_antojito.data.AppDatabase
import com.famessa.deli_antojito.repository.ProductoRepository
import com.famessa.deli_antojito.repository.ProductoRepositoryInterface
import com.famessa.deli_antojito.ui.theme.Deli_antojitoTheme
import com.famessa.deli_antojito.viewModels.HomeViewModel
import com.famessa.deli_antojito.viewModels.HomeViewModelFactory
import com.famessa.deli_antojito.views.HomeView
import com.famessa.products_admin.ProductsAdminActivity

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

        // Crear repositorio
        val repository: ProductoRepositoryInterface = ProductoRepository(database)

        setContent {
            Deli_antojitoTheme(darkTheme = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(repository))
                    HomeView(
                        viewModel = viewModel,
                        onAdminClick = {
                            val intent = Intent(this, ProductsAdminActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ApplicationPreview() {
    Deli_antojitoTheme {
        // Para preview, creamos un ViewModel con un repositorio mock
        val mockRepository = object : ProductoRepositoryInterface {
            override suspend fun insertProducto(producto: com.famessa.deli_antojito.data.Producto) {
                // Mock implementation
            }

            override fun getAllProductos(): kotlinx.coroutines.flow.Flow<List<com.famessa.deli_antojito.data.Producto>> {
                return kotlinx.coroutines.flow.flowOf(emptyList())
            }

            override suspend fun getProductoById(id: Long): com.famessa.deli_antojito.data.Producto? {
                return null
            }

            override suspend fun deleteProducto(id: Long) {
                // Mock implementation
            }
        }
        val viewModel = HomeViewModel(mockRepository)
        HomeView(viewModel)
    }
}
