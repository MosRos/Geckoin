package com.mrostami.geckoin.presentation.base

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.mrostami.geckoin.GeckoinApp
import com.mrostami.geckoin.R
import com.mrostami.geckoin.presentation.utils.AppBarUtils
import kotlinx.coroutines.flow.collect

abstract class BaseActivity<viewModel: BaseActivityViewModel> : AppCompatActivity() {

    abstract val viewModel: BaseActivityViewModel

    private var selectedTheme: Int = GeckoinApp.getInstance().getThemeMode()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(selectedTheme)
        super.onCreate(savedInstanceState)
        configTransition()
        observeThemeChanges()
        applyThemingConfig()
        viewModel.getThemeMode()
    }

    private fun configTransition() {
        overridePendingTransition(R.anim.nav_default_enter_anim, R.anim.nav_default_exit_anim)
    }

    private fun observeThemeChanges() {
        lifecycleScope.launchWhenStarted {
            viewModel.appThemeChanged.collect { changed ->
                selectedTheme = viewModel.selectedThemeMode
                if (changed) {
                    applyTheme()
                }
            }
        }
    }

    private fun applyTheme() {
        this.recreate()
    }

    private fun applyThemingConfig() {
        val mode: Int = viewModel.selectedThemeMode
        if (mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            when (mode) {
                AppCompatDelegate.MODE_NIGHT_YES -> {
                    AppBarUtils.setStatusLightDark(this, false)
                    AppBarUtils.setNavBarLightDark(this, false)
                }

                AppCompatDelegate.MODE_NIGHT_NO -> {
                    AppBarUtils.setStatusLightDark(this, true)
                    AppBarUtils.setNavBarLightDark(this, true)
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                    Configuration.UI_MODE_NIGHT_YES -> {
                        AppBarUtils.setStatusLightDark(this, false)
                        AppBarUtils.setNavBarLightDark(this, false)
                    }

                    Configuration.UI_MODE_NIGHT_NO -> {
                        AppBarUtils.setStatusLightDark(this, true)
                        AppBarUtils.setNavBarLightDark(this, true)
                    }
                }
            } else {
                when (mode) {
                    AppCompatDelegate.MODE_NIGHT_YES -> {
                        AppBarUtils.setStatusLightDark(this, false)
                        AppBarUtils.setNavBarLightDark(this, false)
                    }

                    AppCompatDelegate.MODE_NIGHT_NO -> {
                        AppBarUtils.setStatusLightDark(this, true)
                        AppBarUtils.setNavBarLightDark(this, true)
                    }
                }
            }
        }
    }
}