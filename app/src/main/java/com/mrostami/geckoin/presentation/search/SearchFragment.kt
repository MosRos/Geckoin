package com.mrostami.geckoin.presentation.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.SearchFragmentBinding
import com.mrostami.geckoin.presentation.utils.viewBinding

class SearchFragment: Fragment(R.layout.search_fragment) {

    private val binding: SearchFragmentBinding by viewBinding(SearchFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}