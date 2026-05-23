package com.albrk.shoescare.data.firebase.model

// Import androidx.room DIHAPUS TOTAL karena kita sudah 100% menggunakan Firebase (Cloud)

/**
 * DATA CLASS: SERVICE ITEM
 * Fungsi: Sebagai cetakan (model) data untuk menangkap daftar layanan dari Firebase.
 * Karena Firebase berbasis NoSQL (JSON), kita hanya butuh class Kotlin murni (POJO) tanpa anotasi tabel.
 */
data class ServiceItem(
    // ID unik (Key) yang digenerate otomatis oleh Firebase
    val id: String = "",

    // Nama layanan (misal: "Deep Clean", "Fast Clean")
    val name: String = "",

    // Harga layanan dalam Rupiah
    val price: Int = 0,

    // Nilai default untuk gambar bawaan (fallback jika gambar tidak ada)
    val imageRes: Int = 0,

    // Nama file gambar yang dikirim dari staf untuk dicocokkan dengan drawable pelanggan
    val imageUri: String? = null,

    // KUNCI SINKRONISASI STAF & PELANGGAN
    // Variabel ini untuk membaca status layanan dari Firebase.
    // Default true, tapi jika dimatikan oleh staf, nilainya jadi false
    val isActive: Boolean = true


)