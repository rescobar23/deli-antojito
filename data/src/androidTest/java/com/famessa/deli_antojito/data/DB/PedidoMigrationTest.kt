package com.famessa.deli_antojito.data.DB

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.famessa.deli_antojito.data.db.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import android.database.sqlite.SQLiteConstraintException
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PedidoMigrationTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test fun migratesEverySupportedVersionWithoutLosingExistingData() {
        for (version in 1..3) {
            val name = "pedidos-migration-$version.db"
            context.deleteDatabase(name)
            try {
                val fixture = InstrumentationRegistry.getInstrumentation().context.assets
                    .open("pedidos/legacy-v$version.sql").bufferedReader().use { it.readText() }
                context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use { legacy ->
                    fixture.lineSequence().filterNot { it.trimStart().startsWith("--") }
                        .joinToString("\n").split(';').filter { it.isNotBlank() }
                        .forEach { legacy.execSQL(it) }
                    legacy.version = version
                }
                open(name).withClosed { database ->
                    val sql = database.openHelper.writableDatabase
                    assertEquals(4, sql.version)
                    sql.query("SELECT nombre_negocio, created_at, updated_at FROM configuracion WHERE id=1").use {
                        assertTrue(it.moveToFirst())
                        assertEquals("Negocio fixture", it.getString(0))
                        assertEquals(if (version == 1) 0L else 10L, it.getLong(1))
                        assertEquals(if (version == 1) 0L else 20L, it.getLong(2))
                    }
                    sql.query("SELECT nombre, precio_base FROM productos WHERE id=7").use {
                        assertTrue(it.moveToFirst())
                        assertEquals("Producto fixture", it.getString(0))
                        assertEquals(12.5, it.getDouble(1), 0.0)
                    }
                    sql.query("SELECT siguiente_numero FROM pedido_secuencia WHERE id=1").use {
                        assertTrue(it.moveToFirst())
                        assertEquals(1L, it.getLong(0))
                    }
                    sql.query("PRAGMA foreign_key_check").use { assertEquals(0, it.count) }
                }
            } finally { context.deleteDatabase(name) }
        }
    }

    @Test fun freshAndMigratedSchemasHaveSameColumnsAndIndices() {
        val freshName = "pedidos-fresh.db"
        val migratedName = "pedidos-schema.db"
        context.deleteDatabase(freshName)
        context.deleteDatabase(migratedName)
        try {
            val fixture = InstrumentationRegistry.getInstrumentation().context.assets
                .open("pedidos/legacy-v3.sql").bufferedReader().use { it.readText() }
            context.openOrCreateDatabase(migratedName, Context.MODE_PRIVATE, null).use { legacy ->
                fixture.lineSequence().filterNot { it.trimStart().startsWith("--") }
                    .joinToString("\n").split(';').filter { it.isNotBlank() }.forEach { legacy.execSQL(it) }
                legacy.version = 3
            }
            open(freshName).withClosed { fresh -> open(migratedName).withClosed { migrated ->
                val tables = listOf("pedidos", "pedido_detalle", "pedido_transiciones", "pedido_secuencia")
                for (table in tables) {
                    fun schema(db: AppDatabase, pragma: String): List<String> =
                        db.openHelper.writableDatabase.query("PRAGMA $pragma(`$table`)").use { cursor ->
                            buildList { while (cursor.moveToNext()) {
                                add((0 until cursor.columnCount).joinToString("|") { cursor.getString(it) ?: "NULL" })
                            } }.sorted()
                        }
                    for (pragma in listOf("table_info", "index_list", "foreign_key_list")) {
                        assertEquals("$table $pragma", schema(fresh, pragma), schema(migrated, pragma))
                    }
                }
            } }
        } finally {
            context.deleteDatabase(freshName)
            context.deleteDatabase(migratedName)
        }
    }

    @Test fun enforcesUniquenessAndHistoricalForeignKeys() {
        val name = "pedidos-constraints.db"
        context.deleteDatabase(name)
        try {
            open(name).withClosed { database ->
                val sql = database.openHelper.writableDatabase
                fun insert(number: Int, token: String) = sql.execSQL("""INSERT INTO pedidos
                    (numero, create_token, cliente, cliente_busqueda, numero_busqueda, fecha_creacion,
                    fecha_actualizacion, estado, total_centavos, revision)
                    VALUES (?, ?, 'Fixture', 'fixture', '00001', 1, 1, 'PENDIENTE', 100, 1)""", arrayOf(number, token))
                insert(1, "one")
                assertThrows(SQLiteConstraintException::class.java) { insert(1, "two") }
                assertThrows(SQLiteConstraintException::class.java) { insert(2, "one") }
                assertThrows(SQLiteConstraintException::class.java) {
                    sql.execSQL("INSERT INTO pedido_detalle (pedido_id, producto_id, nombre_producto, cantidad, precio_unitario_centavos, subtotal_centavos, posicion) VALUES (999, 7, 'Fixture', 1, 100, 100, 0)")
                }
                sql.execSQL("INSERT INTO pedido_detalle (pedido_id, producto_id, nombre_producto, cantidad, precio_unitario_centavos, subtotal_centavos, posicion) VALUES (1, 999, 'Producto eliminado', 1, 100, 100, 0)")
                assertThrows(SQLiteConstraintException::class.java) { sql.execSQL("DELETE FROM pedidos WHERE id=1") }
            }
        } finally { context.deleteDatabase(name) }
    }

    private fun open(name: String): AppDatabase = Room.databaseBuilder(context, AppDatabase::class.java, name)
        .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3, AppDatabase.MIGRATION_3_4)
        .addCallback(AppDatabase.CREATE_CALLBACK)
        .build()
}

private inline fun <T> AppDatabase.withClosed(block: (AppDatabase) -> T): T = try { block(this) } finally { close() }

