/*
 * *
 *  * Created by Moslem Rostami on 6/18/20 8:21 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 6/18/20 8:21 PM
 *
 */

package com.mrostami.geckoin.presentation.ranking

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.ListItemRankedCoinBinding
import com.mrostami.geckoin.model.RankedCoin
import com.mrostami.geckoin.presentation.utils.getColour
import com.mrostami.geckoin.presentation.utils.round
import kotlin.math.roundToInt

class RankedCoinsAdapter(
    private val onRankedCoinClick: ((RankedCoin, Int) -> Unit)?
) : PagingDataAdapter<RankedCoin, RankedCoinsAdapter.RankedCoinViewHolder>(COIN_COMPARATOR) {

    companion object {
        private val COIN_COMPARATOR = object : DiffUtil.ItemCallback<RankedCoin>() {
            override fun areItemsTheSame(oldItem: RankedCoin, newItem: RankedCoin): Boolean {
                return (oldItem.id == newItem.id)
            }

            override fun areContentsTheSame(oldItem: RankedCoin, newItem: RankedCoin): Boolean =
                oldItem == newItem
        }
    }

    override fun onBindViewHolder(holder: RankedCoinViewHolder, position: Int) {
        val coin: RankedCoin? = getItem(position)
        coin?.let {
            holder.bind(coin, position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankedCoinViewHolder {
        val binding = ListItemRankedCoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RankedCoinViewHolder(binding, onRankedCoinClick)
    }

    inner class RankedCoinViewHolder(
        val binding: ListItemRankedCoinBinding,
        val itemClickListener: ((RankedCoin, Int) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.rootLayout.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { coin ->
                    itemClickListener?.invoke(coin, bindingAdapterPosition)
                }
            }
        }

        fun bind(coin: RankedCoin, position: Int) {
            with(binding) {
                imgCoinLogo.load(coin.image) {
                    networkCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                    networkCachePolicy(CachePolicy.ENABLED)
                    crossfade(true)
                    placeholder(R.drawable.placeholder_character)
                    transformations(CircleCropTransformation())
                }

                with(txtCoinSymbol) {
                    text = coin.symbol
                    isAllCaps = true
                }
                txtCoinName.text = coin.name
                txtRank.text = coin.marketCapRank?.toString()
                txtPrice.text = coin.currentPrice?.let {
                    getReadablePrice(it)
                }

                txtPriceAmountChange.text = coin.priceChange24h?.let {
                    getReadablePrice(it)
                }
                txtPricePercentChange.text = coin.priceChangePercentage24h?.round(decimals = 2).toString()
                coin.priceChangePercentage24h?.let {
                    applyMarketStateColor(it)
                }
            }
        }

        fun getReadablePrice(price: Double) : String {
            return when {
                price in 1.0..10.0 -> {price.round(decimals = 3).toString()}
                price in 10.0..100.0 -> {price.round(decimals = 2).toString()}
                price in 100.0..1000.0 -> price.round(decimals = 1).toString()
                price > 1000.0 -> price.roundToInt().toString()
                else -> price.toString()
            }
        }

        private fun applyMarketStateColor(change: Double) {
            val red: Int = binding.rootLayout.context?.getColour(R.color.down_red) ?: Color.parseColor("#D32F2F")
            val green: Int = binding.rootLayout.context?.getColour(R.color.up_green) ?: Color.parseColor("#00796B")

            when {
                change > 0 -> {
                    binding.txtPricePercentChange.setTextColor(green)
                }
                change < 0 -> {
                    binding.txtPricePercentChange.setTextColor(red)
                }
            }
        }
    }
}