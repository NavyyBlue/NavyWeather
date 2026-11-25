package com.navyblue.navyweather.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.navyblue.navyweather.shared.di.initKoin
import platform.UIKit.UIViewController
import kotlin.experimental.ExperimentalNativeApi

/** Called from Swift (`MainViewControllerKt.MainViewController()`) to host the Compose UI. */
fun MainViewController(): UIViewController = ComposeUIViewController { App() }

/** Called once from the SwiftUI `App` initializer (`MainViewControllerKt.startKoinIos()`). */
@OptIn(ExperimentalNativeApi::class)
fun startKoinIos() = initKoin(enableHttpLogging = Platform.isDebugBinary)
