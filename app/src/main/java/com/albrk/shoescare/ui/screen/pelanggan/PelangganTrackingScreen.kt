package com.albrk.shoescare.ui.screen.pelanggan

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.albrk.shoescare.data.firebase.model.ServiceItem
import com.albrk.shoescare.data.firebase.model.Transaction
import com.albrk.shoescare.viewmodel.ShoeViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

/**
 * SCREEN: PELANGGAN TRACKING & RESERVASI
 * Fitur: Booking Layanan, Lacak Status Sepatu, & Auto-fill profil.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PelangganTrackingScreen(
    viewModel: ShoeViewModel,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid ?: ""

    // --- 1. OBSERVASI DATA ---
    val activeServices by viewModel.activeServices.collectAsState(initial = emptyList())
    val allTransactions by viewModel.allTransactions.collectAsState(initial = emptyList())

    // --- 2. STATE FORM BOOKING ---
    var inputName by remember { mutableStateOf("") }
    var inputPhone by remember { mutableStateOf("") }
    var inputAddress by remember { mutableStateOf("") }
    var selectedServices by remember { mutableStateOf(setOf<ServiceItem>()) }

    // State Opsi Pengiriman
    val dropOffOptions = listOf("Dijemput Kurir", "Antar Sendiri")
    var selectedDropOff by remember { mutableStateOf(dropOffOptions[0]) }
    val returnOptions = listOf("Diantar Kurir", "Ambil Sendiri")
    var selectedReturn by remember { mutableStateOf(returnOptions[0]) }

    val requiresAddress = selectedDropOff == "Dijemput Kurir" || selectedReturn == "Diantar Kurir"

    // [FITUR CERDAS] Auto-fill data dari profil Firebase
    LaunchedEffect(uid) {
        if (uid.isNotEmpty()) {
            // [PERBAIKAN ERROR MERAH] Menambahkan parameter ke-4 (_)
            // Karena photoUrl tidak dipakai di form ini, kita pakai underscore
            viewModel.getUserProfile(uid) { name, phone, address, _ ->
                inputName = name
                inputPhone = phone
                inputAddress = address
            }
        }
    }

    // --- 3. STATE TRACKING ---
    var searchName by remember { mutableStateOf("") }
    val filteredTransactions = remember(allTransactions, searchName) {
        allTransactions.filter {
            it.customerName.contains(searchName, ignoreCase = true) && searchName.isNotBlank()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ALBRK Reservasi", fontWeight = FontWeight.ExtraBold) },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Profil", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // ================= FORM BOOKING =================
            Text("Buat Pesanan Baru", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = inputName,
                onValueChange = { inputName = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = inputPhone,
                onValueChange = { if (it.all { char -> char.isDigit() }) inputPhone = it },
                label = { Text("Nomor WhatsApp") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Row Opsi Kurir
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Penyerahan:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                    dropOffOptions.forEach { method ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedDropOff = method }) {
                            RadioButton(selected = selectedDropOff == method, onClick = { selectedDropOff = method })
                            Text(text = method, fontSize = 11.sp)
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pengembalian:", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Gray)
                    returnOptions.forEach { method ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { selectedReturn = method }) {
                            RadioButton(selected = selectedReturn == method, onClick = { selectedReturn = method })
                            Text(text = method, fontSize = 11.sp)
                        }
                    }
                }
            }

            if (requiresAddress) {
                OutlinedTextField(
                    value = inputAddress,
                    onValueChange = { inputAddress = it },
                    label = { Text("Alamat Lengkap") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Pilih Layanan:", fontWeight = FontWeight.Medium, fontSize = 14.sp)

            // Katalog Layanan (LazyRow)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                items(activeServices) { service ->
                    val isSelected = selectedServices.contains(service)
                    val resName = service.imageUri?.lowercase()?.replace(" ", "")?.trim() ?: "albrk"
                    val imageResId = context.resources.getIdentifier(resName, "drawable", context.packageName).let {
                        if (it != 0) it else com.albrk.shoescare.R.drawable.albrk
                    }

                    Card(
                        modifier = Modifier.width(130.dp).clickable {
                            selectedServices = if (isSelected) selectedServices - service else selectedServices + service
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = if (isSelected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        colors = CardDefaults.cardColors(containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f) else Color.White),
                        elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp)
                    ) {
                        Column {
                            Image(painter = painterResource(id = imageResId), contentDescription = null, modifier = Modifier.height(80.dp).fillMaxWidth(), contentScale = ContentScale.Crop)
                            Column(Modifier.padding(8.dp)) {
                                Text(service.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                Text("Rp ${service.price}", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Estimasi & Tombol
            val grandTotal = selectedServices.sumOf { it.price }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Estimasi Harga:", color = Color.Gray)
                Text("Rp $grandTotal", fontWeight = FontWeight.Black, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = {
                    if (inputName.isBlank() || inputPhone.isBlank() || (requiresAddress && inputAddress.isBlank())) {
                        Toast.makeText(context, "Lengkapi form!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedServices.isEmpty()) {
                        Toast.makeText(context, "Pilih layanan!", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val gabunganService = selectedServices.joinToString(", ") { it.name }
                    val addr = if (requiresAddress) "[$selectedDropOff & $selectedReturn] - $inputAddress" else "[Antar & Ambil Sendiri]"

                    viewModel.submitBooking(inputName, inputPhone, addr, gabunganService, grandTotal)
                    Toast.makeText(context, "Pesanan Dikirim!", Toast.LENGTH_SHORT).show()
                    searchName = inputName
                    selectedServices = emptySet()
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Booking Sekarang", fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // ================= SEKSI TRACKING =================
            Text("Lacak Status Sepatu", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchName,
                onValueChange = { searchName = it },
                placeholder = { Text("Ketik nama pemesan...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (searchName.isEmpty()) {
                InfoPlaceholder("Masukkan nama Anda untuk melihat status.")
            } else if (filteredTransactions.isEmpty()) {
                InfoPlaceholder("Nama '$searchName' tidak ditemukan.", isError = true)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    filteredTransactions.forEach { transaction -> TrackingCardPremium(transaction) }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// Komponen UI Tambahan (Sama seperti sebelumnya)
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
                    Text(transaction.status, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if(transaction.status == "Selesai") Color(0xFF2E7D32) else if(transaction.status == "Dibatalkan") Color.Red else Color(0xFF1565C0))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(transaction.serviceNames, fontSize = 14.sp)
            Text("Total: Rp ${transaction.totalPrice}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

            Spacer(modifier = Modifier.height(16.dp))
            if (isCancelled) {
                Text("DIBATALKAN", color = Color.Red, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            } else {
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
        Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun RowScope.StatusLine(isActive: Boolean) {
    Box(modifier = Modifier.weight(1f).height(2.dp).background(if (isActive) MaterialTheme.colorScheme.primary else Color.LightGray))
}

@Composable
fun InfoPlaceholder(text: String, isError: Boolean = false) {
    Text(text, Modifier.fillMaxWidth().padding(30.dp), textAlign = TextAlign.Center, color = if (isError) Color.Red else Color.Gray, fontSize = 14.sp)
}