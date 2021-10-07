package com.mrostami.geckoin.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.HomeFragmentBinding
import com.mrostami.geckoin.presentation.utils.viewBinding

class HomeFragment : Fragment(R.layout.home_fragment) {

    private val binding: HomeFragmentBinding by viewBinding(HomeFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}