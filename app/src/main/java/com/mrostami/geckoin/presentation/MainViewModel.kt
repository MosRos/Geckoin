package com.mrostami.geckoin.presentation

import androidx.lifecycle.MutableLiveData
import com.mrostami.geckoin.domain.usecases.ThemeConfigUseCase
import com.mrostami.geckoin.presentation.base.BaseActivityViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val themeConfigUseCase: ThemeConfigUseCase
) : BaseActivityViewModel(themeConfigUseCase) {

    val fragName: MutableLiveData<String> = MutableLiveData("Home")

}