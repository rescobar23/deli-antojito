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
