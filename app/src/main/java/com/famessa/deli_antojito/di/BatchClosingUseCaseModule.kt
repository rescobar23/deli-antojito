package com.famessa.deli_antojito.di
import com.famessa.deli_antojito.domain.repository.CierreConsultaRepository
import com.famessa.deli_antojito.domain.time.*
import com.famessa.deli_antojito.domain.usecase.cierre.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module @InstallIn(SingletonComponent::class)
object BatchClosingUseCaseModule {
    @Provides @com.famessa.deli_antojito.feature.batchclosing.BatchClosingWorker
    fun timeDispatcher(): kotlinx.coroutines.CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default
    @Provides fun validation(resolver: CierreDateTimeResolver) = ValidateCierrePeriodoUseCase(resolver::isValidZone)
    @Provides fun calculation() = CalculateConsultaCierreUseCase()
    @Provides fun query(repository: CierreConsultaRepository, clock: BusinessDayProvider, validate: ValidateCierrePeriodoUseCase, calculate: CalculateConsultaCierreUseCase) =
        GetConsultaCierreUseCase(repository, clock::nowMillis, validate, calculate)
}

