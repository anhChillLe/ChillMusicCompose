package com.chillle.chillmusic.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chillle.chillmusic.viewmodel.PlayerViewModel

@Composable
fun SystemBar() {
    val style = viewModel<PlayerViewModel>().style
    val view = LocalView.current

    val window = remember {
        (view.context as Activity).window
    }
    val wic = remember(window) {
        WindowCompat.getInsetsController(window, view)
    }

    SideEffect {
        window.statusBarColor = style.backgroundColor.toArgb()
        window.navigationBarColor = style.backgroundColor.toArgb()
        wic.isAppearanceLightStatusBars = !style.isDarkBackGround
        wic.isAppearanceLightNavigationBars = !style.isDarkBackGround
    }
}