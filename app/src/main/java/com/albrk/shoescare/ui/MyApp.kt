package com.albrk.shoescare.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import com.albrk.shoescare.ui.screen.pelanggan.PelangganTrackingScreen
import com.albrk.shoescare.ui.screen.pelanggan.ProfileScreen
import com.albrk.shoescare.viewmodel.ShoeViewModel

@Composable
fun MyApp(
    uid: String,
    viewModel: ShoeViewModel,
    onLogout: () -> Unit
) {
    var currentScreen by remember { mutableStateOf("dashboard") }

    if (currentScreen != "dashboard") {
        BackHandler {
            currentScreen = "dashboard"
        }
    }

    when (currentScreen) {
        "dashboard" -> {
            PelangganTrackingScreen(
                viewModel = viewModel,
                onNavigateToProfile = {
                    currentScreen = "profile"
                }
            )
        }

        "profile" -> {
            ProfileScreen(
                uid = uid,
                viewModel = viewModel,
                onNavigateBack = {
                    currentScreen = "dashboard"
                },
                onLogout = onLogout // <--- Pastikan ini sudah tertulis
            )
        }
    }
}