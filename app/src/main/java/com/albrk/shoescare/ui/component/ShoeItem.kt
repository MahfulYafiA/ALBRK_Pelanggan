package com.albrk.shoescare.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.albrk.shoescare.data.firebase.model.Shoe
import java.text.NumberFormat
import java.util.Locale

/**
 * COMPONENT: SHOE ITEM
 * Fungsi: Menampilkan kartu (Card) tunggal untuk satu jenis layanan atau barang.
 * Komponen ini dibuat fleksibel sehingga bisa digunakan di berbagai halaman.
 */
@Composable
fun ShoeItem(
    shoe: Shoe, // Parameter Data: Objek data yang akan ditampilkan
    modifier: Modifier = Modifier, // Parameter Modifier standar untuk fleksibilitas layout UI
    onItemClick: (Shoe) -> Unit = {} // Parameter Event: Fungsi yang dijalankan saat kartu diklik
) {
    // =======================================================
    // OPTIMASI PERFORMA: MEMORI CACHING
    // =======================================================
    // Menggunakan 'remember' agar objek NumberFormat tidak dibuat ulang berkali-kali
    // setiap kali UI digambar ulang (Recomposition). Kalkulasi hanya berjalan jika 'shoe.price' berubah.
    val formattedPrice = remember(shoe.price) {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0 // Menghilangkan angka koma (,00) di belakang harga
        }.format(shoe.price)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick(shoe) }, // Membuat seluruh area kartu bisa diklik
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), // Memberikan efek bayangan (shadow) 3D ringan
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // Warna latar kartu menyesuaikan tema (Light/Dark mode)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                // --- TEKS JUDUL / NAMA LAYANAN ---
                Text(
                    text = shoe.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // --- TEKS HARGA ---
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), // Membuat teks sedikit transparan/abu-abu
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 1, // Mencegah teks memakan lebih dari 1 baris
                    softWrap = false // Mencegah teks turun ke baris baru jika layarnya sempit
                )
            }
        }
    }
}