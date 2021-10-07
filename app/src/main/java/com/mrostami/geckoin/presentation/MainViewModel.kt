/*
 * *
 *  * Created by Moslem Rostami on 3/23/20 10:51 PM
 *  * Copyright (c) 2020 . All rights reserved.
 *  * Last modified 3/23/20 10:51 PM
 *
 */

package com.mrostami.geckoin.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) : ViewModel() {

    val fragName: MutableLiveData<String> = MutableLiveData("Home")

    var selectedThemeMode: Int = 1
    val themeMode: MutableLiveData<Int> = MutableLiveData(1)
    fun changeTheme(mode: Int) {
        selectedThemeMode = mode
        themeMode.value = selectedThemeMode
    }
}