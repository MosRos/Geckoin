package com.mrostami.geckoin.presentation.base

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionInflater
import android.view.Window
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.mrostami.geckoin.GeckoinApp
import com.mrostami.geckoin.R
import com.mrostami.geckoin.presentation.utils.AppBarUtils
import kotlinx.coroutines.flow.collect

abstract class BaseActivity<viewModel : BaseActivityViewModel> : AppCompatActivity() {

    abstract val viewModel: BaseActivityViewModel

    private var selectedTheme: Int = GeckoinApp.getInstance().getThemeMode()

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(selectedTheme)
        super.onCreate(savedInstanceState)
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            enterTransition = TransitionInflater.from(this@BaseActivity).inflateTransition(R.transition.fade_in)
            exitTransition = TransitionInflater.from(this@BaseActivity).inflateTransition(R.transition.fade_out)
        }

        observeThemeChanges()
        applyThemingConfig()
        viewModel.getThemeMode()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        configTransition(isBack = true)
    }

    private fun configTransition(isBack: Boolean = false) {
        if (isBack)
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in)
        else
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
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
        val restartIntent = Intent.makeRestartActivityTask(intent.component).apply {

        }
        startActivity(restartIntent)
        configTransition(isBack = false)
//        this.recreate()
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