package com.mrostami.geckoin.data.local

import com.mrostami.geckoin.data.local.dao.GlobalInfoDao
import com.mrostami.geckoin.data.local.dao.PreferencesDao
import com.mrostami.geckoin.data.local.preferences.PreferencesHelper
import com.mrostami.geckoin.model.*
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val cryptoDataBase: CryptoDataBase,
    private val preferencesHelper: PreferencesHelper
) : PreferencesDao, GlobalInfoDao {

    private val globalInfoDao: GlobalInfoDao by lazy { cryptoDataBase.globalInfoDao() }

    override fun setSelectedTheme(mode: Int) {
        preferencesHelper.selectedThemeMode = mode
    }

    override fun getSelectedTheme(): Int {
        return preferencesHelper.selectedThemeMode
    }

    override fun getAuthToken(): String? {
        return preferencesHelper.authToken
    }

    override fun setAuthToken(token: String) {
        preferencesHelper.authToken = token
    }

    override suspend fun putGlobalInfo(info: GlobalMarketInfo) {
        globalInfoDao.putGlobalInfo(info)
    }

    override suspend fun getGlobalMarketInfo(id: Int): GlobalMarketInfo? {
        return globalInfoDao.getGlobalMarketInfo(id = 1)
    }

    override suspend fun clearGlobalMarketInfo() {
        globalInfoDao.clearGlobalMarketInfo()
    }

    override suspend fun putBtcPriceInfo(info: BitcoinPriceInfo) {
        globalInfoDao.putBtcPriceInfo(info)
    }

    override suspend fun getBtcPriceInfo(id: String): BitcoinPriceInfo? {
        return globalInfoDao.getBtcPriceInfo(id)
    }

    override suspend fun clearBtcPriceInfo() {
        globalInfoDao.clearBtcPriceInfo()
    }

    override suspend fun putPriceEntry(entry: PriceEntry) {
        globalInfoDao.putPriceEntry(entry)
    }

    override suspend fun putPriceEntries(entries: List<PriceEntry>) {
        globalInfoDao.putPriceEntries(entries)
    }

    override suspend fun getPriceChartEntries(id: String): List<PriceEntry> {
        return globalInfoDao.getPriceChartEntries(id = id)
    }

    override suspend fun deletePriceChartEntries(coinId: String) {
        globalInfoDao.deletePriceChartEntries(coinId = coinId)
    }

    override suspend fun clearAllCoinsPriceChartInfo() {
        globalInfoDao.clearAllCoinsPriceChartInfo()
    }

    override suspend fun insertTrendCoin(coin: TrendCoin) {
        globalInfoDao.insertTrendCoin(coin)
    }

    override suspend fun insertTrendCoins(coinsList: List<TrendCoin>) {
        globalInfoDao.insertTrendCoins(coinsList)
    }

    override suspend fun getTrendCoins(): List<TrendCoin> {
        return globalInfoDao.getTrendCoins()
    }

    override suspend fun deleteTrendCoin(coin: TrendCoin) {
        globalInfoDao.deleteTrendCoin(coin)
    }

    override suspend fun deleteTrendCoins(coinsList: List<TrendCoin>) {
        globalInfoDao.deleteTrendCoins(coinsList)
    }

    override suspend fun clearAllTrendCoins() {
        globalInfoDao.clearAllTrendCoins()
    }
}