package com.mrostami.geckoin.presentation.home

import android.view.LayoutInflater
import android.view.View
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

class TrenCoinsAdapter(
    private val onItemClicked: ((TrendCoin, Int) -> Unit)? = null
) : ListAdapter<TrendCoin, TrenCoinsAdapter.TrendCoinsViewHolder>(
    TrendsComparator
) {

    companion object {
        val TrendsComparator = object : DiffUtil.ItemCallback<TrendCoin>() {
            override fun areItemsTheSame(oldItem: TrendCoin, newItem: TrendCoin): Boolean {
                return (oldItem.id == newItem.id)
            }

            override fun areContentsTheSame(oldItem: TrendCoin, newItem: TrendCoin): Boolean {
                return oldItem.equals(newItem)
            }
        }
    }

    private var items: ArrayList<TrendCoin> = arrayListOf()
    fun submitTrendCoins(newItems: List<TrendCoin>) {
        items.clear()
        items.addAll(newItems)
        submitList(items)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendCoinsViewHolder {
        val binding =
            ListItemTrendCoinBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrendCoinsViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: TrendCoinsViewHolder, position: Int) {
        val coin: TrendCoin = items[position]
        holder.bind(coin)
    }

    inner class TrendCoinsViewHolder(
        val binding: ListItemTrendCoinBinding,
        val itemListener: ((TrendCoin, Int) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.rootLayout.setOnClickListener {
                itemListener?.invoke(items[bindingAdapterPosition], bindingAdapterPosition)
            }
        }

        fun bind(coin: TrendCoin) {
            with(binding) {
                imgCoinLogo.load(coin.small){
                    networkCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                    networkCachePolicy(CachePolicy.ENABLED)
                    crossfade(true)
                    placeholder(R.drawable.placeholder_character)
                    transformations(CircleCropTransformation())
                }
                txtCoinSymbol.text = coin.symbol
                txtCoinName.text = coin.name
//                txtCoinPrice.text = coin.priceBtc?.toString() + "(btc)"
                txtRank.text = "# ${coin.marketCapRank}"
            }


        }
    }
}