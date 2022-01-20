package com.mrostami.geckoin.domain.usecases

import com.mrostami.geckoin.domain.AppConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThemeConfigUseCase @Inject constructor(
    private val appConfigRepository: AppConfigRepository
) {
    fun getThemeMode() : Flow<Int> = appConfigRepository.getThemeMode()
    fun changeTheme(mode: Int) = appConfigRepository.changeTheme(mode)
}