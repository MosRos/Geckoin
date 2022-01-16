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
import com.mrostami.geckoin.R
import com.mrostami.geckoin.databinding.SettingsFragmentBinding
import com.mrostami.geckoin.presentation.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val binding: SettingsFragmentBinding by viewBinding(SettingsFragmentBinding::bind)
    val viewModel: SettingsViewModel by viewModels<SettingsViewModel>()

    private var themeDialogBuilder: AlertDialog.Builder? = null
    private var themeDialog: AlertDialog? = null
    private var selectedTheme: Int = AppCompatDelegate.MODE_NIGHT_YES

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerWidgets()
        setObservers()
    }

    private fun registerWidgets() {
        when (selectedTheme) {
            AppCompatDelegate.MODE_NIGHT_NO -> setThemeIcon(R.drawable.ic_sun)

            AppCompatDelegate.MODE_NIGHT_YES -> setThemeIcon(R.drawable.ic_moon)

            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> setThemeIcon(R.drawable.ic_brightness_auto)

            else -> setThemeIcon(R.drawable.ic_brightness_auto)
        }
        binding.menuThemeSetting.rootLayout?.setOnClickListener {
            if (selectedTheme < 0 || selectedTheme > 3) {
                selectedTheme = 3
            }
            showThemeDialog()
        }
        binding.imgLogo.setOnClickListener {
            if (selectedTheme < 0 || selectedTheme > 3) {
                selectedTheme = 3
            }
            showThemeDialog()
        }
    }

    private fun setObservers() {
//        mainViewModel.themeMode.observe(this, Observer { mode ->
//            when (mode) {
//                AppCompatDelegate.MODE_NIGHT_NO -> setThemeIcon(R.drawable.ic_sun)
//
//                AppCompatDelegate.MODE_NIGHT_YES -> setThemeIcon(R.drawable.ic_moon)
//
//                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> setThemeIcon(R.drawable.ic_brightness_auto)
//
//                else -> setThemeIcon(R.drawable.ic_brightness_auto)
//            }
//        })
    }

    private fun setThemeIcon(@DrawableRes iconId: Int) {
//        binding.themeSelectIcon.setImageDrawable(ContextCompat.getDrawable(this, iconId))
    }


    private fun initThemeSelectDialog() {
        context?.let { ctx ->
            themeDialogBuilder = AlertDialog.Builder(ctx)
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

    }

    private fun changeTheme(mode: Int) {
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