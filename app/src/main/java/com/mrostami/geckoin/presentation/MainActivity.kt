package com.mrostami.geckoin.presentation

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.MainActivityBinding
import com.mrostami.geckoin.presentation.base.BaseActivity
import com.mrostami.geckoin.presentation.base.BaseActivityViewModel
import com.mrostami.geckoin.workers.SyncCoinsWorker
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel>() {

    val mainViewModel: MainViewModel by viewModels<MainViewModel>()
    override val viewModel: BaseActivityViewModel
        get() = mainViewModel
    override val layoutResId: Int
        get() = R.layout.main_activity

//    private lateinit var viewBinding: MainActivityBinding
    private var navController: NavController? = null
    private var appBarLayout: AppBarLayout? = null
    private var bottomNav: BottomNavigationView? = null
    private var txtPageTitle: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewBinding = MainActivityBinding.inflate(layoutInflater)
//        val view: View = viewBinding.root

        appBarLayout = findViewById(R.id.appBarLayout)
        bottomNav = findViewById(R.id.bottomNavigation)
        txtPageTitle = findViewById(R.id.txtPageTitle)
        val navHost =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as? NavHostFragment
        navController = navHost?.navController
        navController?.let { initBottomNavAndController(it) }
    }

    override fun onStart() {
        super.onStart()
        initSyncWorker()
    }

    override fun onDestroy() {
        super.onDestroy()
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
        bottomNav?.let { bnav ->
            NavigationUI.setupWithNavController(bnav, navController)
        }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.label != null) {
                mainViewModel.fragName.postValue(destination.label.toString())
                txtPageTitle?.text = destination.label.toString()
            }

            appBarLayout?.isVisible = destination.id != R.id.navigation_search
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.navigateUp() ?: false
    }
}
