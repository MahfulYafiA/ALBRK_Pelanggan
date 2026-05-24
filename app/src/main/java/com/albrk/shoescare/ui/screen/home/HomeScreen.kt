package com.albrk.shoescare.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Pastikan mengimport ServiceItem (data dari Firebase)
import com.albrk.shoescare.data.firebase.model.ServiceItem
import com.albrk.shoescare.ui.component.ShoeItem

/**
 * SCREEN: HOME (PELANGGAN)
 * Fungsi: Murni sebagai Etalase/Katalog Layanan.
 * Fitur Keranjang dan Checkout SUDAH DIHAPUS.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    serviceList: List<ServiceItem>,      // Data yang dipakai sekarang adalah ServiceItem
    onItemClick: (ServiceItem) -> Unit   // Aksi: Saat layanan diklik, pindah ke DetailScreen
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Katalog Layanan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
        // FAB (Tombol Tambah) dan BottomAppBar (Keranjang) sudah kita musnahkan dari sini
    ) { innerPadding ->

        // KONTEN UTAMA (DAFTAR LAYANAN / ETALASE)
        if (serviceList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Layanan sedang dimuat atau belum tersedia.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(items = serviceList) { service ->
                    ShoeItem(
                        service = service, // Mengoper data ServiceItem ke komponen
                        onItemClick = onItemClick
                    )
                }
            }
        }
    }
}