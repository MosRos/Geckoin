package com.mrostami.geckoin.presentation

import android.app.Application
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.MainActivityBinding
import com.mrostami.geckoin.presentation.utils.AppBarUtils
import com.mrostami.geckoin.presentation.workers.SyncCoinsWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels<MainViewModel>()
    private lateinit var viewBinding: MainActivityBinding
    private var themeDialogBuilder: AlertDialog.Builder? = null
    private var themeDialog: AlertDialog? = null
    private var selectedTheme: Int = AppCompatDelegate.MODE_NIGHT_YES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        selectedTheme = mainViewModel.selectedThemeMode
        AppCompatDelegate.setDefaultNightMode(selectedTheme)

        viewBinding = MainActivityBinding.inflate(layoutInflater)
        val view: View = viewBinding.root
        setContentView(view)
        applyThemingConfig()

        val navHost =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as? NavHostFragment
        val navController: NavController? = navHost?.navController
        navController?.setGraph(R.navigation.main_navigation)
        navController?.let { initBottomNavAndController(it) }

//        initSyncWorker()
        registerWidgets()
        setObservers()
        setListeners()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    private fun initSyncWorker() {
        val syncWorkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .setRequiresBatteryNotLow(true)
            .build()
        val syncRequest = OneTimeWorkRequestBuilder<SyncCoinsWorker>()
            .setConstraints(syncWorkConstraints)
            .build()
        WorkManager.getInstance(this).enqueue(syncRequest)
    }

    private fun initBottomNavAndController(navController: NavController) {

        NavigationUI.setupWithNavController(viewBinding.bottomNavigation, navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.label != null) {
                mainViewModel.fragName.postValue(destination.label.toString())
            }

            viewBinding.appBarLayout.isVisible = destination.id != R.id.navigation_search
        }
    }

    private fun registerWidgets() {
        when (selectedTheme) {
            AppCompatDelegate.MODE_NIGHT_NO -> setThemeIcon(R.drawable.ic_sun)

            AppCompatDelegate.MODE_NIGHT_YES -> setThemeIcon(R.drawable.ic_moon)

            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> setThemeIcon(R.drawable.ic_brightness_auto)

            else -> setThemeIcon(R.drawable.ic_brightness_auto)
        }
    }

    private fun setObservers() {
        mainViewModel.themeMode.observe(this, Observer { mode ->
            when (mode) {
                AppCompatDelegate.MODE_NIGHT_NO -> setThemeIcon(R.drawable.ic_sun)

                AppCompatDelegate.MODE_NIGHT_YES -> setThemeIcon(R.drawable.ic_moon)

                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> setThemeIcon(R.drawable.ic_brightness_auto)

                else -> setThemeIcon(R.drawable.ic_brightness_auto)
            }
        })
    }

    private fun setThemeIcon(@DrawableRes iconId: Int) {
        viewBinding.themeSelectIcon.setImageDrawable(ContextCompat.getDrawable(this, iconId))
    }

    private fun setListeners() {
        viewBinding.themeSelectIcon.setOnClickListener {
            if (selectedTheme < 0 || selectedTheme > 3) {
                selectedTheme = 3
            }
            showThemeDialog()
        }
    }

    private fun initThemeSelectDialog() {
        themeDialogBuilder = AlertDialog.Builder(this)
            .setTitle("Please Select Theme")
            .setCancelable(true)
            .setSingleChoiceItems(
                arrayOf("Light", "Dark", "Auto"),
                selectedTheme - 1
            ) { dialogInterface: DialogInterface?, i: Int ->
                changeTheme(i)
            }

        themeDialog = themeDialogBuilder?.create()
    }

    private fun showThemeDialog() {
        if (themeDialog == null) {
            initThemeSelectDialog()
        }
        themeDialog?.show()
    }

    private fun changeTheme(mode: Int) {
        themeDialog?.dismiss()
        lifecycleScope.launch {
            delay(100)
            when (mode) {
                0 -> mainViewModel.selectedThemeMode = 1
                1 -> mainViewModel.selectedThemeMode = 2
                2 -> mainViewModel.selectedThemeMode = -1
                else -> mainViewModel.selectedThemeMode = -1
            }
            applyTheme()
        }
    }

    private fun applyTheme() {
        this.recreate()
    }

    private fun applyThemingConfig() {
        val mode: Int = selectedTheme
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
