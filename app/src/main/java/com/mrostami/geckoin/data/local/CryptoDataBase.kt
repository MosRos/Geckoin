package com.mrostami.geckoin.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mrostami.geckoin.data.local.dao.CryptoRanksDao
import com.mrostami.geckoin.data.local.dao.GlobalInfoDao
import com.mrostami.geckoin.data.local.dao.RemoteKeysDao
import com.mrostami.geckoin.model.*

@Database(
    entities = [
        Coin::class,
        RankedCoin::class,
        CoinsRemoteKeys::class,
        GlobalMarketInfo::class,
        TrendCoin::class,
        BitcoinPriceInfo::class,
        EthereumPriceInfo::class,
        PriceEntry::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class CryptoDataBase : RoomDatabase() {

    abstract fun globalInfoDao(): GlobalInfoDao
    abstract fun ranksDao() : CryptoRanksDao
    abstract fun remoteKeysDao() : RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: CryptoDataBase? = null
        private const val DB_NAME = "geckoin_db"

        fun getInstance(context: Context): CryptoDataBase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, CryptoDataBase::class.java, DB_NAME)
                .build()
    }
}