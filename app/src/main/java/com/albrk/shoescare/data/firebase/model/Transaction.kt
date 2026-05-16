package com.albrk.shoescare.data.firebase.model

// Import Room Database telah dihapus sepenuhnya untuk migrasi arsitektur Cloud-Native

/**
 * DATA CLASS: TRANSACTION
 * Fungsi: Model data utama untuk membungkus seluruh detail pesanan pelanggan
 * sebelum dikirimkan (push) ke Firebase Realtime Database.
 */
data class Transaction(
    // ID lokal (Opsional, bisa dibiarkan sebagai fallback bawaan)
    val id: Int = 0,

    // Data Identitas Pelanggan
    val customerName: String = "",
    val customerPhone: String = "", // Menyimpan nomor WhatsApp untuk kemudahan hubungi pelanggan
    val address: String = "",       // Menyimpan alamat untuk keperluan layanan antar-jemput (Pick-up/Delivery)

    // Data Pesanan
    val serviceNames: String = "",  // Gabungan nama layanan dari keranjang (Cth: "Nike (Deep Clean), Adidas (Unyellowing)")
    val totalPrice: Int = 0,        // Total tagihan dalam Rupiah

    // Metadata Transaksi
    val date: Long = System.currentTimeMillis(), // Waktu pesanan dibuat (dalam format Unix Timestamp / milisecond)

    // Status Tracking (Sangat Penting)
    // Default "Diajukan" agar saat masuk ke database, staf tahu ini pesanan baru yang belum diproses.
    val status: String = "Diajukan",

    // Kunci unik langsung dari Firebase (Node Key)
    // Sangat krusial agar aplikasi pelanggan bisa melacak perubahan status spesifik untuk pesanan ini.
    val firebaseKey: String = ""
)