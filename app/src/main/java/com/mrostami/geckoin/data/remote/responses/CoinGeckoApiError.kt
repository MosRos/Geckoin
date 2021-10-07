package com.mrostami.geckoin.data.remote.responses

import com.google.gson.annotations.SerializedName

data class CoinGeckoApiError(
    @SerializedName("error")
    var error: String? = "invalid"
)