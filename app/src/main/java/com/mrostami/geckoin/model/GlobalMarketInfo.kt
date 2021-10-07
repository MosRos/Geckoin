/*
 * *
 *  * Created by Moslem Rostami on 7/25/20 9:46 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 7/25/20 9:46 PM
 *
 */

package com.mrostami.geckoin.model

data class GlobalMarketInfo(
//    val id: Long,
    var activeCryptocurrencies: Int = 0,
    var markets: Int = 0,
    var endedIcos: Int = 0,
    var ongoingIcos: Int = 0,
    var upcomingIcos: Int = 0,
    var marketCapChangePercentage24hUsd: Double = 0.0,
    var marketCapPercentage: Map<String, Double>? = null,
    var totalMarketCapUsd: Double = 0.0,
    var totalVolumeUsd: Double = 0.0,
    var updatedAt: Long = 0
)