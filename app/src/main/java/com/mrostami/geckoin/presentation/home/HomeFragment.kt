package com.mrostami.geckoin.presentation.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.HomeFragmentBinding
import com.mrostami.geckoin.domain.base.Result
import com.mrostami.geckoin.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {

    private val binding: HomeFragmentBinding by viewBinding(HomeFragmentBinding::bind)
    val viewModel: HomeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getGlobalInfo(forceRefresh = false)
        viewModel.getTrendCoins(forceRefresh = false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerWidgets()
        setObservables()
    }

    private fun registerWidgets() {

    }

    private fun setObservables() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.marketInfoState.collect { result ->
                when(result) {
                    is Result.Empty -> {
                        binding.progressBar.isVisible = false
                    }
                    is Result.Success -> {
                        binding.progressBar.isVisible = false
                        Timber.e("Success globalInfo Result is: ${result.data}")
                    }
                    is Result.Error -> {
                        binding.progressBar.isVisible = false
                        Timber.e("Global Info Error: ${result.message}")
                        Toast.makeText(requireContext(), "Error getting global info: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.trendCoinsState.collect { result ->
                when(result) {
                    is Result.Empty -> {
                        binding.progressBar.isVisible = false
                    }
                    is Result.Success -> {
                        binding.progressBar.isVisible = false
                        Timber.e("TrendCoins Result is: ${result.data}")
                    }
                    is Result.Error -> {
                        binding.progressBar.isVisible = false
                        Timber.e("TrendCoins Error: ${result.message}")
                        Toast.makeText(requireContext(), "Error Getting Trend coins: ${result.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                }
            }
        }
    }
}