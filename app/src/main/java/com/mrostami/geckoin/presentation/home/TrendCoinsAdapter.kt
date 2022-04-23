package com.mrostami.geckoin.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.ListItemTrendCoinBinding
import com.mrostami.geckoin.model.TrendCoin

class TrendCoinsAdapter(
    private val onItemClicked: ((TrendCoin, Int) -> Unit)? = null
) : ListAdapter<TrendCoin, TrendCoinsAdapter.TrendCoinsViewHolder>(
    TrendsComparator
) {

    companion object {
        val TrendsComparator = object : DiffUtil.ItemCallback<TrendCoin>() {
            override fun areItemsTheSame(oldItem: TrendCoin, newItem: TrendCoin): Boolean {
                return (oldItem.id == newItem.id)
            }

            override fun areContentsTheSame(oldItem: TrendCoin, newItem: TrendCoin): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendCoinsViewHolder {
        val binding =
            ListItemTrendCoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrendCoinsViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: TrendCoinsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrendCoinsViewHolder(
        val binding: ListItemTrendCoinBinding,
        val itemListener: ((TrendCoin, Int) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.rootLayout.setOnClickListener {
                itemListener?.invoke(getItem(bindingAdapterPosition), bindingAdapterPosition)
            }
        }

        fun bind(coin: TrendCoin) {
            with(binding) {
                imgCoinLogo.load(coin.small) {
                    networkCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                    networkCachePolicy(CachePolicy.ENABLED)
                    crossfade(true)
                    placeholder(R.drawable.placeholder_character)
                    transformations(CircleCropTransformation())
                }
                txtCoinSymbol.text = coin.symbol
                txtCoinName.text = coin.name
                txtRank.text = "# ${coin.marketCapRank}"
            }


        }
    }
}