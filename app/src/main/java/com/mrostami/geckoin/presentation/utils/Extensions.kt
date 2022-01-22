package com.mrostami.geckoin.presentation.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mrostami.geckoin.GeckoinApp
import com.mrostami.geckoin.R
import java.lang.Exception
import java.text.DecimalFormat

fun Context.showToast(
    message: String,
    length: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, message, length).show()
}

fun Context.getColour(@ColorRes colorResId: Int) : Int {
    return ContextCompat.getColor(this, colorResId)
}

fun Activity.showSnack(
    message: String,
    length: Int = Snackbar.LENGTH_SHORT,
    actionText: String = "Ok",
    actionTextColorResId: Int = android.R.color.holo_red_dark,
    action: (() -> Unit)? = null
): Snackbar? {
    val view = findViewById<View>(android.R.id.content)
    view?.let {
        val snack = Snackbar.make(view, message, length)
        if (action != null) {
            snack.setAction(actionText) {
                action.invoke()
            }
            snack.setActionTextColor(ContextCompat.getColor(this, actionTextColorResId))
        }
        snack.show()
        return snack
    }
    return null
}

fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

fun Long.decimalFormat(pattern: String = "#,###,###,###") : String? {
    val df = DecimalFormat(pattern)
    return try {
        df.format(this)
    } catch (e: Exception) {
        Log.e("DecimalFormat", e.message.toString())
        null
    }
}

fun Int.decimalFormat(pattern: String = "#,###,###,###") : String? {
    val df = DecimalFormat(pattern)
    return try {
        df.format(this)
    } catch (e: Exception) {
        Log.e("DecimalFormat", e.message.toString())
        null
    }
}