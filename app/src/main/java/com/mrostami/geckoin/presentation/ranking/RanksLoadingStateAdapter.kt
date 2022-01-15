package com.mrostami.geckoin.presentation.ranking

import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mrostami.geckoin.databinding.RanksLoadingStateBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class RanksLoadingStateAdapter(
    val onRetryClicked: () -> Unit,
    val scope: CoroutineScope?
) : LoadStateAdapter<RanksLoadingStateAdapter.LoadingStateViewHolder> () {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val binding: RanksLoadingStateBinding = RanksLoadingStateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }


    inner class LoadingStateViewHolder(
        private val binding: RanksLoadingStateBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val handler = Handler()
        val hideProgressRunnable = Runnable {
            binding.progressBar.isVisible = false
        }

        init {
            binding.retryButton.setOnClickListener {
                onRetryClicked.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            Timber.e("RanksLoadState: $loadState")
            with(binding) {
                if (loadState == LoadState.Loading) {
                    progressBar.isVisible = true
                    handler.removeCallbacks(hideProgressRunnable)
                } else {
                    if (scope != null) {
                        scope.launch {
                            delay(1000)
                            progressBar.isVisible = false
                        }
                    }
                    handler.postDelayed(hideProgressRunnable, 1000)
                }
                retryButton.isVisible = loadState is LoadState.Error
                errorMsg.isVisible =
                    !(loadState as? LoadState.Error)?.error?.message.isNullOrBlank()
                errorMsg.text = (loadState as? LoadState.Error)?.error?.message
            }
        }
    }
}