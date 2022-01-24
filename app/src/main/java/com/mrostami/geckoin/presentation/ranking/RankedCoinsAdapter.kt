/*
 * *
 *  * Created by Moslem Rostami on 6/18/20 8:21 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 6/18/20 8:21 PM
 *
 */

package com.mrostami.geckoin.presentation.ranking

import android.view.LayoutInflater
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
import com.mrostami.geckoin.presentation.utils.applyPriceStateTextColor
import com.mrostami.geckoin.presentation.utils.round
import com.mrostami.geckoin.presentation.utils.toReadablePrice

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
        val binding =
            ListItemRankedCoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                txtPrice.text = coin.currentPrice?.toReadablePrice()

                txtPriceAmountChange.text = coin.priceChange24h?.toReadablePrice()
                txtPricePercentChange.text =
                    coin.priceChangePercentage24h?.round(decimals = 2).toString()
                txtPricePercentChange.applyPriceStateTextColor(coin.priceChangePercentage24h)
            }
        }
    }
}