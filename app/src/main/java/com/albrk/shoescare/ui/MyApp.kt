package com.albrk.shoescare.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.albrk.shoescare.data.firebase.model.ServiceItem

// Pastikan import ini sesuai dengan nama folder/package di laptop Boss
import com.albrk.shoescare.ui.screen.detail.DetailScreen
import com.albrk.shoescare.ui.screen.home.HomeScreen
import com.albrk.shoescare.ui.screen.pelanggan.PelangganTrackingScreen
import com.albrk.shoescare.ui.screen.pelanggan.ProfileScreen
import com.albrk.shoescare.viewmodel.ShoeViewModel

@Composable
fun MyApp(
    uid: String,
    viewModel: ShoeViewModel,
    onLogout: () -> Unit
) {
    // State untuk menyimpan posisi layar saat ini (Default: home)
    var currentScreen by remember { mutableStateOf("home") }

    // State untuk menyimpan data sepatu yang sedang di-klik ke DetailScreen
    var selectedService by remember { mutableStateOf<ServiceItem?>(null) }

    // Menarik data layanan langsung dari ViewModel
    val activeServices by viewModel.activeServices.collectAsState(initial = emptyList())

    // Logika Tombol Back (Kembali) di HP
    if (currentScreen != "home") {
        BackHandler {
            currentScreen = "home"
        }
    }

    Scaffold(
        bottomBar = {
            // Menu Navigasi Bawah disembunyikan saat sedang melihat DetailScreen
            if (currentScreen != "detail") {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = { currentScreen = "home" },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Katalog") },
                        label = { Text("Katalog") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "tracking",
                        onClick = { currentScreen = "tracking" },
                        icon = { Icon(Icons.Default.List, contentDescription = "Pesanan") },
                        label = { Text("Pesanan") }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "profile",
                        onClick = { currentScreen = "profile" },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                        label = { Text("Profil") }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Pembungkus agar layar tidak tertutup oleh Menu Bawah
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "home" -> {
                    HomeScreen(
                        serviceList = activeServices,
                        onItemClick = { service ->
                            selectedService = service // Simpan data yang diklik
                            currentScreen = "detail"  // Pindah ke layar detail
                        }
                    )
                }
                "detail" -> {
                    selectedService?.let { service ->
                        DetailScreen(
                            name = service.name,
                            price = service.price,
                            navigateBack = { currentScreen = "home" }
                        )
                    }
                }
                "tracking" -> {
                    PelangganTrackingScreen(
                        uid = uid,                 // <--- uid sekarang dimasukkan
                        viewModel = viewModel
                    )
                }
                "profile" -> {
                    ProfileScreen(
                        uid = uid,
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = "home" },
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}