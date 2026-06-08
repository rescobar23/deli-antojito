package com.famessa.deli_antojito.di

import com.famessa.deli_antojito.domain.repository.ProductoRepository
import com.famessa.deli_antojito.domain.usecase.producto.DeleteProductoUseCase
import com.famessa.deli_antojito.domain.usecase.producto.GetProductoByIdUseCase
import com.famessa.deli_antojito.domain.usecase.producto.ObserveProductosUseCase
import com.famessa.deli_antojito.domain.usecase.producto.SaveProductoUseCase
import com.famessa.deli_antojito.domain.usecase.producto.ValidateProductoNameAvailabilityUseCase
import com.famessa.deli_antojito.domain.usecase.producto.ValidateProductoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ProductoUseCaseModule {
    @Provides
    fun provideValidateProductoUseCase(): ValidateProductoUseCase = ValidateProductoUseCase()

    @Provides
    fun provideObserveProductosUseCase(repository: ProductoRepository): ObserveProductosUseCase {
        return ObserveProductosUseCase(repository)
    }

    @Provides
    fun provideGetProductoByIdUseCase(repository: ProductoRepository): GetProductoByIdUseCase {
        return GetProductoByIdUseCase(repository)
    }

    @Provides
    fun provideValidateProductoNameAvailabilityUseCase(
        repository: ProductoRepository
    ): ValidateProductoNameAvailabilityUseCase {
        return ValidateProductoNameAvailabilityUseCase(repository)
    }

    @Provides
    fun provideSaveProductoUseCase(
        repository: ProductoRepository,
        validateProductoUseCase: ValidateProductoUseCase
    ): SaveProductoUseCase {
        return SaveProductoUseCase(repository, validateProductoUseCase)
    }

    @Provides
    fun provideDeleteProductoUseCase(repository: ProductoRepository): DeleteProductoUseCase {
        return DeleteProductoUseCase(repository)
    }
}
