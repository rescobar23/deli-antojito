package com.famessa.deli_antojito.data.DAO

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.famessa.deli_antojito.data.db.AppDatabase
import com.famessa.deli_antojito.data.entities.Producto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductoDaoTest {
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertsAndListsProducts() = runTest {
        database.productoDao().insertProducto(Producto(nombre = "Alitas", precioBase = 120.0, activo = true))

        assertEquals(1, database.productoDao().getAllProductos().first().size)
    }

    @Test
    fun findsDuplicateNameIgnoringCaseAndSpaces() = runTest {
        database.productoDao().insertProducto(Producto(nombre = "Alitas", precioBase = 120.0))

        assertNotNull(database.productoDao().getProductoByNormalizedName(" alitas ", null))
    }

    @Test
    fun updateAllowsSameIdNameReuse() = runTest {
        val id = database.productoDao().insertProducto(Producto(nombre = "Alitas", precioBase = 120.0))

        assertNull(database.productoDao().getProductoByNormalizedName("alitas", id))
    }

    @Test
    fun physicallyDeletesProduct() = runTest {
        val id = database.productoDao().insertProducto(Producto(nombre = "Alitas", precioBase = 120.0))

        assertEquals(1, database.productoDao().deleteProducto(id))
        assertEquals(emptyList<Producto>(), database.productoDao().getAllProductos().first())
    }
}
