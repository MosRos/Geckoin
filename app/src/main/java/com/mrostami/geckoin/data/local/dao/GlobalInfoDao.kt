package com.mrostami.geckoin.data.local.dao

import androidx.room.*
import com.mrostami.geckoin.model.GlobalMarketInfo
import com.mrostami.geckoin.model.TrendCoin

@Dao
interface GlobalInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putGlobalInfo(info: GlobalMarketInfo)

    @Query("SELECT * FROM GlobalMarketInfo WHERE id = :id")
    suspend fun getGlobalMarketInfo(id: Int) : GlobalMarketInfo?

    @Query("DELETE FROM GlobalMarketInfo")
    suspend fun clearGlobalMarketInfo()

    // Trend Coins
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrendCoin(coin: TrendCoin)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrendCoins(coinsList: List<TrendCoin>)

    @Query("SELECT * FROM TrendCoin")
    suspend fun getTrendCoins(): List<TrendCoin>

    @Delete
    suspend fun deleteTrendCoin(coin: TrendCoin)

    @Delete
    suspend fun deleteTrendCoins(coinsList: List<TrendCoin>)

    @Query("DELETE FROM TrendCoin")
    suspend fun clearAllTrendCoins()
}