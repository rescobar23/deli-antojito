package com.famessa.deli_antojito.data.fixtures

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.famessa.deli_antojito.data.db.AppDatabase

/** Uses real SQLite; test-only triggers inject failures inside production transactions. */
class PedidoDatabaseFixture(name: String? = null) : AutoCloseable {
    val context: Context = ApplicationProvider.getApplicationContext()
    val database: AppDatabase = (if (name == null) Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
        else Room.databaseBuilder(context, AppDatabase::class.java, name))
        .addCallback(AppDatabase.CREATE_CALLBACK).build()

    fun failLineInsert() {
        database.openHelper.writableDatabase.execSQL(
            "CREATE TEMP TRIGGER fail_line BEFORE INSERT ON pedido_detalle BEGIN SELECT RAISE(ABORT, 'fixture failure'); END"
        )
    }

    fun failTransitionInsert() {
        database.openHelper.writableDatabase.execSQL(
            "CREATE TEMP TRIGGER fail_transition BEFORE INSERT ON pedido_transiciones BEGIN SELECT RAISE(ABORT, 'fixture failure'); END"
        )
    }

    override fun close() = database.close()
}
