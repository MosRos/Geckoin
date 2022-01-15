/*
 * *
 *  * Created by Moslem Rostami on 4/14/20 9:15 AM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/26/20 11:35 PM
 *
 */

package com.mrostami.geckoin.presentation.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mrostami.geckoin.databinding.ListItemCoinBinding
import com.mrostami.geckoin.model.Coin

class CoinsAdapter(
    val onCoinClick: (Coin) -> Unit
) : PagingDataAdapter<Coin, CoinsAdapter.CoinsViewHolder>(COINS_COMPARATOR) {

    companion object {
        private val COINS_COMPARATOR = object : DiffUtil.ItemCallback<Coin>() {
            override fun areItemsTheSame(oldItem: Coin, newItem: Coin): Boolean {
                return (oldItem.id == newItem.id)
            }

            override fun areContentsTheSame(oldItem: Coin, newItem: Coin): Boolean =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsViewHolder {
        val binding = ListItemCoinBinding.inflate(LayoutInflater.from(parent.context))
        return CoinsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinsViewHolder, position: Int) {
        val coin: Coin? = getItem(position)
        coin?.let {
            holder.bind(it)
        }
    }

    inner class CoinsViewHolder(val binding: ListItemCoinBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.rootLayout.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { coin ->
                    onCoinClick.invoke(coin)
                }
            }
        }
        fun bind(coin: Coin) {
            with(binding) {
                txtCoinName.text = coin.name
                txtCoinSymbol.text = coin.symbol
            }
        }
    }
}