package com.mrostami.geckoin.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mrostami.geckoin.data.local.CryptoDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context) : SharedPreferences {
        return context.getSharedPreferences("geckoin_app_prefs", Context.MODE_PRIVATE)
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) : CryptoDataBase {
        return CryptoDataBase.getInstance(context)
    }

    @Provides
    fun provideGson() : Gson {
        return GsonBuilder().create()
    }
}