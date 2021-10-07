package com.mrostami.geckoin.di

import android.content.Context
import androidx.annotation.NonNull
import com.haroldadmin.cnradapter.NetworkResponseAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val BASE_URL: String = "https://api.coingecko.com/api/v3/"

    @Singleton
    @Provides
    fun okHttpCache(context: Context): Cache {
        return Cache(context.cacheDir, 10 * 1000 * 1000)
    }

    @Provides
    @Singleton
    fun loggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
//            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
    }


    @Singleton
    @Provides
    fun provideHttpClient(@NonNull loggingInterceptor: HttpLoggingInterceptor) : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    @Named("httpClientCache")
    fun provideHttpClientWithCache(@NonNull loggingInterceptor: HttpLoggingInterceptor, cache: Cache) : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .cache(cache)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(@NonNull httpClient: OkHttpClient) : Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .build()
    }
}