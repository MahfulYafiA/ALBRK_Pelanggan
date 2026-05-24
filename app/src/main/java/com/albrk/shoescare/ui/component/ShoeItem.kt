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
// IMPORT YANG BENAR HANYA SERVICE ITEM:
import com.albrk.shoescare.data.firebase.model.ServiceItem
import java.text.NumberFormat
import java.util.Locale

/**
 * COMPONENT: SHOE ITEM
 * Fungsi: Menampilkan kartu (Card) tunggal untuk satu jenis layanan di Katalog.
 */
@Composable
fun ShoeItem(
    service: ServiceItem, // Diubah menjadi murni ServiceItem
    modifier: Modifier = Modifier,
    onItemClick: (ServiceItem) -> Unit = {} // Aksi klik sekarang membawa data ServiceItem
) {
    // =======================================================
    // OPTIMASI PERFORMA: MEMORI CACHING
    // =======================================================
    val formattedPrice = remember(service.price) {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }.format(service.price)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onItemClick(service) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
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
                    text = service.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // --- TEKS HARGA ---
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp),
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}