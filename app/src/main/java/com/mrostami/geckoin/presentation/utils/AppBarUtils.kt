package com.mrostami.geckoin.presentation.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.mrostami.geckoin.R


object AppBarUtils {

    fun setLightStatusBar(view: View, activity: Activity, colorRsId: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            var flags = view.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.systemUiVisibility = flags

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val decorView = activity.window.decorView
                decorView.systemUiVisibility =
                    WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }

        } else {
            activity.window.statusBarColor =
                ContextCompat.getColor(activity.baseContext, R.color.transparent)
            activity.window.navigationBarColor =
                ContextCompat.getColor(activity.baseContext, R.color.transparent)
        }
    }

    fun clearLightStatusBar(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = activity.window
            window.statusBarColor = ContextCompat.getColor(activity, R.color.color_surface)
        }
    }

    fun clearLightStatusBar(@NonNull view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            view.systemUiVisibility = flags
        }
    }

    fun setStatusBarColor(activity: Activity, color: Int) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.setStatusBarColor(color)
    }

    fun setStatusLightDark(activity: Activity, is_light: Boolean) {
        if (Build.VERSION.SDK_INT < 23) {
            return
        }
        var flags = activity.window.decorView.systemUiVisibility
        if (is_light) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        activity.window.decorView.systemUiVisibility = flags
    }

    fun setNavBarColor(activity: Activity, color: Int) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.setNavigationBarColor(color)
    }

    fun setNavBarLightDark(activity: Activity, is_light: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        var flags = activity.window.decorView.systemUiVisibility
        if (is_light) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
        activity.window.decorView.systemUiVisibility = flags
    }
}