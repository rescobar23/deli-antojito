package com.famessa.deli_antojito.data.cierre
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.famessa.deli_antojito.data.db.AppDatabase
import com.famessa.deli_antojito.data.entities.Pedido
import com.famessa.deli_antojito.data.entities.PedidoDetalle

internal class CierreRoomFixtures(name: String? = null) : AutoCloseable {
    val database = (if(name == null) Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java) else Room.databaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java, name)).addCallback(AppDatabase.CREATE_CALLBACK).build()
    suspend fun insert(created: Long, state: String = "COMPLETADO", lines: Boolean = true): Long {
        val id = database.pedidoDao().insert(Pedido(numero=created, createToken="$created-$state", cliente="Fixture", clienteBusqueda="fixture", numeroBusqueda=created.toString(), fechaCreacion=created, fechaActualizacion=9000, estado=state,totalCentavos=200,observaciones=null,revision=0))
        if(lines) database.pedidoDao().insertLines(listOf(PedidoDetalle(pedidoId=id,productoId=7,nombreProducto="Histórico",cantidad=2,precioUnitarioCentavos=100,subtotalCentavos=200,posicion=0)))
        return id
    }
    override fun close() = database.close()
}

