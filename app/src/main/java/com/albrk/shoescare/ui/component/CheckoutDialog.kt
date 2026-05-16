package com.albrk.shoescare.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * COMPONENT: CHECKOUT DIALOG
 * Fungsi: Menampilkan pop-up konfirmasi pembayaran dan meminta input nama pelanggan
 * sebelum transaksi dikirim ke database (Firebase).
 */
@Composable
fun CheckoutDialog(
    totalPrice: Int,               // Parameter Data: Total harga yang harus dibayar
    onDismiss: () -> Unit,         // Parameter Event: Aksi jika dialog ditutup/batal
    onConfirm: (String) -> Unit    // Parameter Event: Aksi jika tombol simpan ditekan (mengirim nama)
) {
    // State lokal untuk menyimpan teks yang sedang diketik oleh pelanggan
    var customerName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss, // Menutup dialog jika area di luar kotak diklik
        shape = RoundedCornerShape(16.dp), // Memberikan efek membulat (rounded) agar terlihat modern
        title = {
            Text(
                text = "Konfirmasi Pembayaran",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            // Column menyusun teks total harga dan kolom input secara vertikal (atas-bawah)
            Column {
                Text(
                    text = "Total yang harus dibayar:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Rp $totalPrice",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold // Harga ditebalkan agar menjadi fokus utama
                )

                Spacer(modifier = Modifier.height(16.dp)) // Memberi jarak antara harga dan kolom input

                // Input teks untuk nama pelanggan
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Nama Pelanggan") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true, // Mencegah teks turun ke baris baru (Enter)
                    shape = RoundedCornerShape(12.dp)
                )

                // *Catatan untuk pengembangan selanjutnya:
                // Jika ingin menambahkan input No WhatsApp atau Alamat, bisa ditambahkan OutlinedTextField baru di sini.
            }
        },
        confirmButton = {
            // Tombol "Simpan Transaksi" (atau "Pesan Sekarang")
            Button(
                onClick = { onConfirm(customerName) },
                // VALIDASI KEAMANAN: Tombol hanya bisa diklik jika nama TIDAK kosong
                enabled = customerName.isNotBlank()
            ) {
                Text("Kirim Pesanan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            // Tombol "Batal"
            TextButton(onClick = onDismiss) {
                Text("Batal", color = MaterialTheme.colorScheme.error) // Warna merah untuk indikasi batal
            }
        }
    )
}