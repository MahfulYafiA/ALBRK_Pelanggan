package com.albrk.shoescare.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.albrk.shoescare.data.firebase.model.Shoe
import com.albrk.shoescare.ui.component.CheckoutDialog
import com.albrk.shoescare.ui.component.ShoeItem
import java.text.NumberFormat
import java.util.Locale

/**
 * SCREEN: HOME (PELANGGAN)
 * Fungsi: Menampilkan etalase layanan yang tersedia, keranjang belanja dinamis,
 * dan memicu proses checkout ke Firebase.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    shoeList: List<Shoe>,           // Data: Daftar layanan/sepatu yang ditampilkan
    cartCount: Int,                 // Data: Jumlah barang di dalam keranjang
    totalPrice: Int,                // Data: Total harga keranjang
    onAddClick: () -> Unit,         // Aksi: Saat tombol tambah (FAB) diklik
    onItemClick: (Shoe) -> Unit,    // Aksi: Saat satu kartu layanan diklik
    onCheckoutClick: (String) -> Unit // Aksi: Saat proses checkout selesai (mengirim nama pelanggan)
) {
    // =======================================================
    // 1. STATE MANAGEMENT LOKAL
    // =======================================================
    // Mengontrol apakah pop-up konfirmasi pembayaran (CheckoutDialog) harus muncul atau tidak.
    var showDialog by remember { mutableStateOf(false) }

    // =======================================================
    // 2. OPTIMASI FORMAT MATA UANG
    // =======================================================
    // Menggunakan 'remember' agar format Rupiah tidak dikalkulasi ulang setiap detik
    // saat user men-scroll layar. Hanya dihitung ulang JIKA totalPrice berubah.
    val formattedTotal = remember(totalPrice) {
        NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply {
            maximumFractionDigits = 0
        }.format(totalPrice)
    }

    // =======================================================
    // 3. CONDITIONAL RENDERING (DIALOG POP-UP)
    // =======================================================
    // Jetpack Compose akan menggambar CheckoutDialog HANYA JIKA showDialog bernilai true.
    if (showDialog) {
        CheckoutDialog(
            totalPrice = totalPrice,
            onDismiss = { showDialog = false }, // Menutup pop-up jika batal
            onConfirm = { customerName ->
                onCheckoutClick(customerName) // Mengirim data ke ViewModel untuk di-push ke Firebase
                showDialog = false // Menutup pop-up setelah sukses
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "ALBRK SHOESCARE",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            // Tombol Melayang untuk aksi utama (bisa diarahkan ke Lacak Pesanan / Tambah Custom)
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Menu Tambahan"
                )
            }
        },
        bottomBar = {
            // =======================================================
            // 4. KERANJANG BELANJA DINAMIS (DYNAMIC BOTTOM BAR)
            // =======================================================
            // BottomAppBar (Keranjang) HANYA muncul jika ada minimal 1 barang yang dipilih.
            // Jika keranjang kosong, area ini akan hilang dan layar menjadi lebih luas.
            if (cartCount > 0) {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "$cartCount Layanan Terpilih",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = formattedTotal,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        // Tombol Bayar memicu State showDialog menjadi true
                        Button(onClick = { showDialog = true }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Text(modifier = Modifier.padding(start = 8.dp), text = "Bayar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // =======================================================
        // 5. KONTEN UTAMA (DAFTAR LAYANAN / ETALASE)
        // =======================================================
        if (shoeList.isEmpty()) {
            // Tampilan jika data layanan dari Firebase masih kosong / loading
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
            // LazyColumn untuk menampilkan data yang banyak tanpa membuat HP lag
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp) // Bottom padding agar tidak tertutup BottomBar
            ) {
                // PERBAIKAN: Menghapus key = { it.id } untuk mencegah crash jika ID duplikat dari default data class
                items(items = shoeList) { shoe ->
                    ShoeItem(
                        shoe = shoe,
                        onItemClick = onItemClick
                    )
                }
            }
        }
    }
}