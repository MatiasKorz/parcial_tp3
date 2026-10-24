package com.example.examentp3.di

import com.example.examentp3.BuildConfig
import com.example.examentp3.data.remote.interceptor.MockScenarioInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Request and response bodies only reach Logcat in debug builds.
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        mockScenarioInterceptor: MockScenarioInterceptor,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .apply {
                // Mock scenarios are a development aid for RF05; release builds never send them.
                // Added before logging so Logcat shows the headers actually sent.
                if (BuildConfig.DEBUG) addInterceptor(mockScenarioInterceptor)
            }
            .addInterceptor(loggingInterceptor)
            .build()
}
