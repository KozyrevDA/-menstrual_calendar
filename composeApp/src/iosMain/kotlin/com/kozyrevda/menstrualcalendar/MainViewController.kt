package com.kozyrevda.menstrualcalendar

import androidx.compose.ui.window.ComposeUIViewController
import com.kozyrevda.menstrualcalendar.app.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
