package com.mrostami.geckoin.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrostami.geckoin.domain.usecases.ThemeConfigUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeConfigUseCase: ThemeConfigUseCase
) : ViewModel() {


    fun changeAppTheme(mode: Int) {
        viewModelScope.launch {
            themeConfigUseCase.changeTheme(mode = mode)
        }
    }
}