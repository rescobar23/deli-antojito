package com.famessa.deli_antojito.data.DAO

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.famessa.deli_antojito.data.db.AppDatabase
import com.famessa.deli_antojito.data.entities.Configuracion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ConfiguracionDaoTest {
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
    fun zeroRowsAreObservable() = runTest {
        assertEquals(emptyList<Configuracion>(), database.configuracionDao().observeConfiguraciones().first())
    }

    @Test
    fun oneRowCanBeSavedAndRead() = runTest {
        database.configuracionDao().upsertConfiguracion(
            Configuracion(id = 1, nombreNegocio = "Deli", createdAt = 1L, updatedAt = 1L)
        )

        assertEquals(1, database.configuracionDao().countConfiguraciones())
        assertEquals("Deli", database.configuracionDao().getConfiguraciones().single().nombreNegocio)
    }

    @Test
    fun duplicateRowsCanBeDetectedWhenDatabaseIsCorrupt() = runTest {
        database.openHelper.writableDatabase.execSQL(
            "INSERT INTO configuracion (id, nombre_negocio, created_at, updated_at) VALUES (1, 'Uno', 1, 1)"
        )
        database.openHelper.writableDatabase.execSQL(
            "INSERT INTO configuracion (id, nombre_negocio, created_at, updated_at) VALUES (2, 'Dos', 1, 1)"
        )

        assertEquals(2, database.configuracionDao().countConfiguraciones())
    }

    @Test
    fun upsertReplacesSingletonRow() = runTest {
        database.configuracionDao().upsertConfiguracion(
            Configuracion(id = 1, nombreNegocio = "Antes", createdAt = 1L, updatedAt = 1L)
        )
        database.configuracionDao().upsertConfiguracion(
            Configuracion(id = 1, nombreNegocio = "Despues", createdAt = 1L, updatedAt = 2L)
        )

        val rows = database.configuracionDao().getConfiguraciones()
        assertEquals(1, rows.size)
        assertEquals("Despues", rows.single().nombreNegocio)
    }
}
