package com.albrk.shoescare.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.albrk.shoescare.data.firebase.model.Shoe

/**
 * COMPONENT: CART ITEM
 * Fungsi: Merupakan 'Reusable Component' (komponen yang bisa dipakai berulang-ulang)
 * untuk menampilkan satu baris item layanan di dalam keranjang belanja pelanggan.
 */
@Composable
fun CartItem(
    shoe: Shoe, // Parameter Data: Menerima objek sepatu/layanan yang dipilih
    onRemoveClick: () -> Unit // Parameter Event: Menerima perintah/fungsi saat tombol silang diklik
) {
    // Row bertindak seperti wadah horizontal (menyusun elemen dari kiri ke kanan)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp), // Padding disesuaikan agar lebih rapi
        horizontalArrangement = Arrangement.SpaceBetween, // Mendorong teks ke kiri dan tombol ke kanan
        verticalAlignment = Alignment.CenterVertically // Memastikan teks dan tombol sejajar di tengah
    ) {
        // Column bertindak seperti wadah vertikal untuk menyusun Nama dan Harga (atas-bawah)
        // modifier.weight(1f) memastikan teks mengambil sisa ruang kosong agar tidak menabrak tombol silang
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = shoe.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium // Teks nama layanan dibuat sedikit lebih tebal
            )
            Text(
                text = "Rp ${shoe.price}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary, // Mengikuti warna tema utama aplikasi
                fontWeight = FontWeight.Bold // Harga ditebalkan agar mudah dibaca pelanggan
            )
        }

        // Tombol aksi untuk menghapus item dari keranjang
        IconButton(onClick = onRemoveClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Hapus dari keranjang",
                tint = MaterialTheme.colorScheme.error // Warnanya otomatis merah (error color) standar Material 3
            )
        }
    }
}