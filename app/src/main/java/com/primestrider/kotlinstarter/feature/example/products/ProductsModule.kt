package com.primestrider.kotlinstarter.feature.example.products

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductsModule {
    @Provides
    @Singleton
    fun productsApi(retrofit: Retrofit): ProductsApi = retrofit.create(ProductsApi::class.java)
}
