package com.mrostami.geckoin.presentation.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.SettingsFragmentBinding
import com.mrostami.geckoin.presentation.utils.viewBinding

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val binding: SettingsFragmentBinding by viewBinding(SettingsFragmentBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}