package com.mrostami.geckoin.presentation.base

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrostami.geckoin.GeckoinApp
import com.mrostami.geckoin.domain.usecases.ThemeConfigUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseActivityViewModel constructor(
    private val themeConfigUseCase: ThemeConfigUseCase
) : ViewModel() {

    var selectedThemeMode: Int = AppCompatDelegate.getDefaultNightMode()
    private val _appThemeChanged = Channel<Boolean>(Channel.BUFFERED)
    val appThemeChanged = _appThemeChanged.receiveAsFlow()
    fun getThemeMode() {
        viewModelScope.launch {
            themeConfigUseCase.getThemeMode().collectLatest { mode ->
                if (selectedThemeMode != mode) {
                    selectedThemeMode = mode
                    _appThemeChanged.send(true)
                }
            }
        }
    }
}