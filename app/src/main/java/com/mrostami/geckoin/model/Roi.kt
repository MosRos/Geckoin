/*
 * *
 *  * Created by Moslem Rostami on 4/10/20 8:33 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 4/10/20 8:33 PM
 *
 */

package com.mrostami.geckoin.model


import com.google.gson.annotations.SerializedName

data class Roi(
    @SerializedName("currency")
    var currency: String? = null, // btc
    @SerializedName("percentage")
    var percentage: Double? = null, // 2943.3353518814183
    @SerializedName("times")
    var times: Double? = null // 29.433353518814183
)