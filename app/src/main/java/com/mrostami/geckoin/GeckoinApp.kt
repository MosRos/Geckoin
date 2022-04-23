package com.mrostami.geckoin

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import com.mrostami.geckoin.data.local.preferences.DataStoreHelper
import com.mrostami.geckoin.data.local.preferences.PreferencesHelper
import com.mrostami.geckoin.di.ApplicationModule
import com.mrostami.geckoin.workers.SyncCoinsWorker
import dagger.hilt.android.HiltAndroidApp
import org.jetbrains.annotations.NonNls
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class GeckoinApp : Application(), Configuration.Provider {

    companion object {
        const val DEFAULT_THEME_MODE: Int = AppCompatDelegate.MODE_NIGHT_NO

        private lateinit var instance: GeckoinApp
            private set
        fun getInstance() : GeckoinApp {
            return instance
        }
    }

    // workerFactory initialized in app Manifest with a provider
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return if (BuildConfig.DEBUG) {
            Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .setMinimumLoggingLevel(Log.DEBUG)
                .build()
        } else {
            Configuration.Builder()
                .setMinimumLoggingLevel(Log.ERROR)
                .setWorkerFactory(workerFactory)
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initTimber()
        initAppConfig()
//        initSyncWorker(this)
    }

    fun getAppContext() : Application {
        return instance
    }

    fun initAppConfig() {
        AppCompatDelegate.setDefaultNightMode(DEFAULT_THEME_MODE)
    }

    private fun initSyncWorker(context: Application) {
        val syncWorkConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiredNetworkType(NetworkType.NOT_ROAMING)
            .setRequiresBatteryNotLow(true)
            .build()
        val syncRequest = OneTimeWorkRequestBuilder<SyncCoinsWorker>()
            .setConstraints(syncWorkConstraints)
            .build()
        WorkManager.getInstance(context).enqueue(syncRequest)
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashReportingTree())
        }
    }

    /** A tree which logs important information for crash reporting.  */
    private class CrashReportingTree : Timber.Tree() {
        override fun log(
            priority: Int,
            tag: String?, @NonNls message: String,
            t: Throwable?
        ) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return
            }
        }
    }
}