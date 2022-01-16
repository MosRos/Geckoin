package com.mrostami.geckoin.di

import com.mrostami.geckoin.data.local.LocalDataSource
import com.mrostami.geckoin.data.remote.RemoteDataSource
import com.mrostami.geckoin.data.repositories.*
import com.mrostami.geckoin.domain.AllCoinsRepository
import com.mrostami.geckoin.domain.AppConfigRepository
import com.mrostami.geckoin.domain.GlobalInfoRepository
import com.mrostami.geckoin.domain.MarketRanksRepository
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
    fun provideAppConfigRepository(
        localDataSource: LocalDataSource
    ) : AppConfigRepository {
        return AppConfigRepositoryImpl(localDataSource = localDataSource)
    }

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

    @Singleton
    @Provides
    fun provideMarketRanksRepository(
        localDataSource: LocalDataSource,
        marketRanksMediator: MarketRanksMediator
    ) : MarketRanksRepository {
            return MarketRanksRepositoryImpl(
                localDataSource = localDataSource,
                marketRanksMediator = marketRanksMediator
            )
    }

    @Singleton
    @Provides
    fun provideAllCoinsRepository(
        localDataSource: LocalDataSource,
        remoteDataSource: RemoteDataSource
    ) : AllCoinsRepository {
        return AllCoinsRepositoryImpl(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource
        )
    }
}