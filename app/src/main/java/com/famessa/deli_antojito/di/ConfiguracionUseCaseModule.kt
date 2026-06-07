package com.famessa.deli_antojito.di

import com.famessa.deli_antojito.domain.repository.ConfiguracionRepository
import com.famessa.deli_antojito.domain.usecase.configuracion.GetStartupConfigurationStateUseCase
import com.famessa.deli_antojito.domain.usecase.configuracion.ObserveConfiguracionUseCase
import com.famessa.deli_antojito.domain.usecase.configuracion.SaveConfiguracionUseCase
import com.famessa.deli_antojito.domain.usecase.configuracion.ValidateBusinessNameUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ConfiguracionUseCaseModule {
    @Provides
    fun provideValidateBusinessNameUseCase(): ValidateBusinessNameUseCase {
        return ValidateBusinessNameUseCase()
    }

    @Provides
    fun provideGetStartupConfigurationStateUseCase(
        repository: ConfiguracionRepository
    ): GetStartupConfigurationStateUseCase {
        return GetStartupConfigurationStateUseCase(repository)
    }

    @Provides
    fun provideSaveConfiguracionUseCase(
        repository: ConfiguracionRepository,
        validateBusinessNameUseCase: ValidateBusinessNameUseCase
    ): SaveConfiguracionUseCase {
        return SaveConfiguracionUseCase(repository, validateBusinessNameUseCase)
    }

    @Provides
    fun provideObserveConfiguracionUseCase(
        repository: ConfiguracionRepository
    ): ObserveConfiguracionUseCase {
        return ObserveConfiguracionUseCase(repository)
    }
}
