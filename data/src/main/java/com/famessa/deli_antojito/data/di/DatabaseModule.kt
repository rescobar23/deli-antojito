package com.famessa.deli_antojito.data.di

import android.content.Context
import androidx.room.Room
import com.famessa.deli_antojito.data.db.AppDatabase
import com.famessa.deli_antojito.data.dao.ProductoDao
import com.famessa.deli_antojito.data.dao.ConfiguracionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "deli_antojito_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideProductoDao(database: AppDatabase): ProductoDao {
        return database.productoDao()
    }

    @Provides
    @Singleton
    fun provideConfiguracionDao(database: AppDatabase): ConfiguracionDao {
        return database.configuracionDao()
    }
}
