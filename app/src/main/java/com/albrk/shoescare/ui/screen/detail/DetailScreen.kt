package com.albrk.shoescare.ui.screen.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

/**
 * SCREEN: DETAIL
 * Fungsi: Menampilkan informasi lengkap mengenai layanan yang dipilih pelanggan.
 * Memiliki fitur kembali (Back) dan aksi tambahan (seperti Hapus dari pilihan/keranjang).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    name: String,
    price: Int,
    navigateBack: () -> Unit, // Callback untuk tombol panah kembali (Back)
    onDeleteClick: () -> Unit // Callback untuk tombol hapus/batal di bawah
) {
    // =======================================================
    // 1. OPTIMASI FORMAT MATA UANG
    // =======================================================
    // Menggunakan 'remember' agar objek NumberFormat tidak membebani RAM
    // dengan melakukan kalkulasi berulang kali saat layar digambar ulang (Recomposition).
    val formattedPrice = remember(price) {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }.format(price)
    }

    // Scaffold menyediakan struktur kerangka dasar UI Material Design (TopBar, BottomBar, Content)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Layanan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    // Tombol Panah Kiri (Back)
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Column Utama pembungkus seluruh konten
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // =======================================================
            // 2. KARTU INFORMASI (CARD)
            // =======================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp), // Sedikit bayangan agar menonjol
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp), // Padding di dalam kartu diperbesar agar lebih lega
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // --- NAMA LAYANAN ---
                    Text(
                        text = "Nama Layanan",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Garis pemisah horizontal (Standar Material 3)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // --- HARGA LAYANAN ---
                    Text(
                        text = "Harga",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formattedPrice,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold, // Harga ditebalkan
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // =======================================================
            // 3. PENGATUR TATA LETAK FLEKSIBEL (SPACER WEIGHT)
            // =======================================================
            // Memberikan weight(1f) pada Spacer yang kosong berfungsi seperti pegas.
            // Ia akan mendorong komponen di bawahnya (Tombol Hapus) hingga mentok ke bagian paling bawah layar.
            Spacer(modifier = Modifier.weight(1f))

            // =======================================================
            // 4. TOMBOL AKSI (HAPUS / BATAL)
            // =======================================================
            Button(
                onClick = onDeleteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                // Menggunakan skema warna error (biasanya merah) sebagai penanda aksi destruktif
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hapus dari Pilihan", fontWeight = FontWeight.Bold)
            }
        }
    }
}