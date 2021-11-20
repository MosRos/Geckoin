package com.mrostami.geckoin.data.local.dao

import androidx.room.*
import com.mrostami.geckoin.model.GlobalMarketInfo

@Dao
interface GlobalInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putGlobalInfo(info: GlobalMarketInfo)

    @Query("SELECT * FROM GlobalMarketInfo WHERE id = :id")
    suspend fun getGlobalMarketInfo(id: Int) : GlobalMarketInfo?

    @Query("DELETE FROM GlobalMarketInfo")
    suspend fun clearGlobalMarketInfo()
}