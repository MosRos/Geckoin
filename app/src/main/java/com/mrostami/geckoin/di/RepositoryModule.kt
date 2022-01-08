package com.mrostami.geckoin.di

import com.mrostami.geckoin.data.local.LocalDataSource
import com.mrostami.geckoin.data.remote.RemoteDataSource
import com.mrostami.geckoin.data.repositories.GlobalInfoRepositoryImpl
import com.mrostami.geckoin.domain.GlobalInfoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideGlobalInfoRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ) : GlobalInfoRepository {
        return GlobalInfoRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource
        )
    }
}