package com.primestrider.kotlinstarter.feature.example.todos

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TodosModule {
    @Provides
    @Singleton
    fun exampleDatabase(
        @ApplicationContext context: Context,
    ): ExampleDatabase = Room.databaseBuilder(context, ExampleDatabase::class.java, "example.db").build()

    @Provides
    fun todoDao(database: ExampleDatabase): TodoDao = database.todoDao()
}
