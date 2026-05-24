package com.albrk.shoescare.ui.screen.pelanggan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.albrk.shoescare.data.firebase.model.Transaction
import com.albrk.shoescare.viewmodel.ShoeViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * SCREEN: PELANGGAN TRACKING
 * Fitur: Murni hanya melacak status sepatu yang sudah dipesan.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PelangganTrackingScreen(
    uid: String,
    viewModel: ShoeViewModel
) {
    // Menarik data transaksi khusus user ini saja (myTransactions)
    val myTransactions by viewModel.myTransactions.collectAsState(initial = emptyList())
    var userPhone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Mengambil nomor HP dari profil untuk dipakai melacak pesanan
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            viewModel.getUserProfile(uid) { _, phone, _, _ ->
                userPhone = phone
                viewModel.fetchMyTransactions(phone)
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pesanan Saya", fontWeight = FontWeight.ExtraBold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                InfoPlaceholder("Memuat data pesanan...")
            } else if (userPhone.isEmpty()) {
                InfoPlaceholder("Gagal memuat profil. Silakan atur nomor WhatsApp di menu Profil.")
            } else if (myTransactions.isEmpty()) {
                InfoPlaceholder("Belum ada pesanan aktif atas nomor\n$userPhone")
            } else {
                Text(
                    text = "Riwayat Pesanan ($userPhone)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Daftar Pesanan menggunakan LazyColumn agar bisa di-scroll dengan rapi
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 30.dp)
                ) {
                    items(myTransactions) { transaction ->
                        TrackingCardPremium(transaction)
                    }
                }
            }
        }
    }
}

// ==========================================
// KOMPONEN UI TAMBAHAN (Desain Premium)
// ==========================================
@Composable
fun TrackingCardPremium(transaction: Transaction) {
    val currentStep = when (transaction.status) {
        "Diajukan" -> 1
        "Diproses" -> 2
        "Selesai" -> 3
        else -> 0
    }
    val isCancelled = transaction.status == "Dibatalkan"
    val formattedDate = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")).format(Date(transaction.date))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(transaction.customerName, fontWeight = FontWeight.Bold)
                    Text(formattedDate, fontSize = 11.sp, color = Color.Gray)
                }
                Surface(
                    color = when(transaction.status) {
                        "Selesai" -> Color(0xFFE8F5E9)
                        "Dibatalkan" -> Color(0xFFFFEBEE)
                        else -> Color(0xFFE3F2FD)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = transaction.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if(transaction.status == "Selesai") Color(0xFF2E7D32) else if(transaction.status == "Dibatalkan") Color.Red else Color(0xFF1565C0)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(transaction.serviceNames, fontSize = 14.sp)
            Text("Total: Rp ${transaction.totalPrice}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(16.dp))
            if (isCancelled) {
                Text("DIBATALKAN", color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            } else {
                // Indikator Progress Status
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    StatusPoint("Diajukan", currentStep >= 1)
                    StatusLine(currentStep >= 2)
                    StatusPoint("Proses", currentStep >= 2)
                    StatusLine(currentStep >= 3)
                    StatusPoint("Selesai", currentStep >= 3)
                }
            }
        }
    }
}

@Composable
fun RowScope.StatusPoint(label: String, isActive: Boolean) {
    val color = if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun RowScope.StatusLine(isActive: Boolean) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(2.dp)
            .padding(horizontal = 4.dp)
            .background(if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray)
    )
}

@Composable
fun InfoPlaceholder(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            modifier = Modifier.padding(30.dp),
            textAlign = TextAlign.Center,
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}