package com.mrostami.geckoin.presentation.market

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.MarketRankFragmentBinding
import com.mrostami.geckoin.presentation.utils.viewBinding

class MarketRanksFragment : Fragment(R.layout.market_rank_fragment) {
    private val binding: MarketRankFragmentBinding by viewBinding(MarketRankFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}