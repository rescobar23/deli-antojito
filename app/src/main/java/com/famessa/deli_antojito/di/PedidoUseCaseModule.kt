package com.famessa.deli_antojito.di

import com.famessa.deli_antojito.domain.repository.*
import com.famessa.deli_antojito.domain.time.BusinessDayProvider
import com.famessa.deli_antojito.domain.usecase.pedido.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module @InstallIn(SingletonComponent::class)
object PedidoUseCaseModule {
    @Provides fun observeOrders(repository: PedidoRepository) = ObservePedidosUseCase(repository)
    @Provides fun observeOrder(repository: PedidoRepository) = ObservePedidoUseCase(repository)
    @Provides fun create(repository: PedidoRepository) = CreatePedidoUseCase(repository)
    @Provides fun update(repository: PedidoRepository) = UpdatePedidoUseCase(repository)
    @Provides fun change(repository: PedidoRepository) = ChangePedidoStateUseCase(repository)
    @Provides fun capture(repository: ProductoRepository) = CapturePedidoProductUseCase(repository)
    @Provides fun summary(repository: PedidoRepository, clock: BusinessDayProvider) = ObserveDailyOrdersSummaryUseCase(repository, clock)
}
