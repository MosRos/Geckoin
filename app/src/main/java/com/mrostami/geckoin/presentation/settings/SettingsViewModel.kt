package com.mrostami.geckoin.presentation.settings

import androidx.lifecycle.ViewModel
import com.mrostami.geckoin.domain.usecases.ThemeConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeConfigUseCase: ThemeConfigUseCase
) : ViewModel() {


    fun changeAppTheme(mode: Int) {
        themeConfigUseCase.changeTheme(mode = mode)
    }
}