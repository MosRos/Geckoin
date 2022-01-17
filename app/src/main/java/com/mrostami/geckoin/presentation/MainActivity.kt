package com.mrostami.geckoin.presentation

import android.os.Bundle
import android.view.View
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

    private lateinit var viewBinding: MainActivityBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = MainActivityBinding.inflate(layoutInflater)
        val view: View = viewBinding.root
        setContentView(view)


        val navHost =
            supportFragmentManager.findFragmentById(R.id.mainNavHost) as? NavHostFragment
        navController = navHost?.navController
        navController?.setGraph(R.navigation.main_navigation)
        navController?.let { initBottomNavAndController(it) }

        initSyncWorker()
    }

    override fun onStart() {
        super.onStart()
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
        NavigationUI.setupWithNavController(viewBinding.bottomNavigation, navController)
        viewBinding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.label != null) {
                mainViewModel.fragName.postValue(destination.label.toString())
                viewBinding.txtPageTitle.text = destination.label.toString()
            }

            val menuIds = listOf<Int>(
                R.id.navigation_home,
                R.id.navigation_market,
                R.id.navigation_search,
                R.id.navigation_settings
            )
            if (destination.id in menuIds) {
                val btmId: Int = viewBinding.bottomNavigation.selectedItemId
                val destId: Int = destination.id
                if (btmId != destId) {
                    viewBinding.bottomNavigation.selectedItemId = destination.id
                }
            }

            viewBinding.appBarLayout.isVisible = destination.id != R.id.navigation_search
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController?.navigateUp() ?: false
    }
}
