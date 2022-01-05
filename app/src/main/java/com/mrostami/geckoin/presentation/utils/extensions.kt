package com.mrostami.geckoin.presentation.utils

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()
