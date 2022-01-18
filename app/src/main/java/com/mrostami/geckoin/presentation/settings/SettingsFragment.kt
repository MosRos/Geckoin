package com.mrostami.geckoin.presentation.settings

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.SettingsFragmentBinding
import com.mrostami.geckoin.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val binding: SettingsFragmentBinding by viewBinding(SettingsFragmentBinding::bind)
    val viewModel: SettingsViewModel by viewModels<SettingsViewModel>()

    private var themeDialogBuilder: AlertDialog.Builder? = null
    private var themeDialog: AlertDialog? = null
    private var selectedTheme: Int = AppCompatDelegate.getDefaultNightMode()

    var themeModePairs: Array<Pair<Int, String>> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themeModePairs = arrayOf(
            AppCompatDelegate.MODE_NIGHT_NO to getString(R.string.light),
            AppCompatDelegate.MODE_NIGHT_YES to getString(R.string.dark),
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM to getString(R.string.system_default),
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY to getString(R.string.auto_battery)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerWidgets()
        setObservers()
    }

    private fun registerWidgets() {
        applyThemeIcon()
        with(binding) {
            imgLogo.load(R.mipmap.ic_launcher) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_foreground)
                transformations(CircleCropTransformation())
            }
            menuThemeSetting.description =
                themeModePairs.map { it.second }.getOrNull(getCheckedItemPosition())
            menuThemeSetting.rootLayout?.setOnClickListener {
                showThemeDialog()
            }
        }
    }

    private fun setObservers() {

    }

    private fun setThemeIcon(@DrawableRes iconId: Int) {
        context?.let { ctx ->
            binding.menuThemeSetting.menuIcon = ContextCompat.getDrawable(ctx, iconId)
        }
    }

    fun applyThemeIcon() {
        when (selectedTheme) {
            AppCompatDelegate.MODE_NIGHT_NO -> setThemeIcon(R.drawable.ic_sun)

            AppCompatDelegate.MODE_NIGHT_YES -> setThemeIcon(R.drawable.ic_moon)

            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> setThemeIcon(R.drawable.ic_brightness_auto)

            else -> setThemeIcon(R.drawable.ic_brightness_auto)
        }
    }


    private fun initThemeSelectDialog() {
        context?.let { ctx ->
            themeDialogBuilder = MaterialAlertDialogBuilder(ctx, R.style.AppTheme_AlertDialogTheme)
                .setTitle("Please Select Theme")
                .setCancelable(true)
                .setSingleChoiceItems(
                    themeModePairs.map { it.second }.toTypedArray(),
                    getCheckedItemPosition(),
                ) { dialogInterface: DialogInterface?, i: Int ->
                    changeTheme(i)
                }

            themeDialog = themeDialogBuilder?.create()
        }

    }

    private fun getCheckedItemPosition(): Int {
        return try {
            themeModePairs.map { it.first }.indexOf(selectedTheme)
        } catch (e: Exception) {
            Timber.e(e)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    private fun changeTheme(position: Int) {
        val mode: Int = themeModePairs.map { it.first }[position]
        themeDialog?.dismiss()
        lifecycleScope.launch {
            delay(100)
            viewModel.changeAppTheme(mode)
        }
    }

    private fun showThemeDialog() {
        if (themeDialog == null) {
            initThemeSelectDialog()
        }
        themeDialog?.show()
    }
}