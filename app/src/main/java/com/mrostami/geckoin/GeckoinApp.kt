package com.mrostami.geckoin

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import org.jetbrains.annotations.NonNls
import timber.log.Timber


@HiltAndroidApp
class GeckoinApp : Application() {

    companion object {
        private lateinit var instance: GeckoinApp
        private set

        fun getInstance() : GeckoinApp {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        initTimber()
    }

    fun getAppContext() : Application {
        return instance
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