package com.albrk.shoescare.data.firebase.model

// Import androidx.room.Entity dan PrimaryKey DIHAPUS karena tidak pakai database lokal lagi

/**
 * DATA CLASS: SHOE (ITEM KERANJANG)
 * Fungsi: Sebagai model data sementara (In-Memory) untuk menyimpan
 * layanan-layanan yang dipilih pelanggan ke dalam keranjang sebelum di-checkout.
 */
data class Shoe(
    // ID lokal untuk membedakan urutan item di dalam keranjang StateFlow (opsional)
    val id: Int = 0,

    // Nilai default "" (String kosong) wajib ada agar Firebase tidak error (No-Argument Constructor)
    // Menyimpan kombinasi detail. Contoh: "Sneakers (Deep Clean)"
    val name: String = "",

    // Nilai default 0 wajib ada agar aman saat dijumlahkan di keranjang
    val price: Int = 0
)