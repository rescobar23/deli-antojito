package com.famessa.deli_antojito.data.di

import com.famessa.deli_antojito.data.repository.ConfiguracionRepositoryImpl
import com.famessa.deli_antojito.data.repository.ProductoRepositoryImpl
import com.famessa.deli_antojito.domain.repository.ConfiguracionRepository
import com.famessa.deli_antojito.domain.repository.ProductoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindCierreReader(implementation: com.famessa.deli_antojito.data.repository.CierreConsultaRepositoryImpl): com.famessa.deli_antojito.domain.repository.CierreConsultaRepository
    @Binds @Singleton
    abstract fun bindCierreTime(implementation: com.famessa.deli_antojito.data.time.LocalCierreDateTimeResolver): com.famessa.deli_antojito.domain.time.CierreDateTimeResolver
    @Binds
    @Singleton
    abstract fun bindPedidoRepository(implementation: com.famessa.deli_antojito.data.repository.PedidoRepositoryImpl): com.famessa.deli_antojito.domain.repository.PedidoRepository

    @Binds
    @Singleton
    abstract fun bindBusinessDayProvider(implementation: com.famessa.deli_antojito.data.time.LocalBusinessDayProvider): com.famessa.deli_antojito.domain.time.BusinessDayProvider

    @Binds
    @Singleton
    abstract fun bindProductoRepository(
        productoRepositoryImpl: ProductoRepositoryImpl
    ): ProductoRepository

    @Binds
    @Singleton
    abstract fun bindConfiguracionRepository(
        configuracionRepositoryImpl: ConfiguracionRepositoryImpl
    ): ConfiguracionRepository
}
