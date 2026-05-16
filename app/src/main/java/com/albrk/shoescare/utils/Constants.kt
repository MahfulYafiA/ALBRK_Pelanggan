package com.albrk.shoescare.utils

/**
 * OBJECT CONSTANTS (PELANGGAN)
 * Fungsi: Sebagai pusat penyimpanan variabel statis (tidak berubah) untuk menghindari typo
 * (salah ketik) dan memudahkan perubahan data secara global.
 */
object Constants {

    // =======================================================
    // 1. FIREBASE CONFIGURATION
    // Menggantikan Room Database lama. Ini adalah jalur utama ke server awan kita.
    // =======================================================
    const val FIREBASE_URL = "https://albrk-shoescare-default-rtdb.asia-southeast1.firebasedatabase.app"
    const val REF_TRANSACTIONS = "transactions"       // Node untuk menyimpan pesanan
    const val REF_MASTER_SERVICES = "master_services" // Node untuk membaca daftar layanan aktif

    // =======================================================
    // 2. NAVIGATION ROUTES
    // Rute halaman khusus untuk sisi Pelanggan
    // =======================================================
    const val ROUTE_LOGIN = "login"
    const val ROUTE_DASHBOARD = "dashboard"

    // =======================================================
    // 3. TRANSACTION STATUS (STANDARISASI STATUS)
    // Digunakan agar penulisan status di seluruh aplikasi tidak ada yang typo (salah huruf besar/kecil)
    // =======================================================
    const val STATUS_PENDING = "Diajukan"
    const val STATUS_PROCESS = "Diproses"
    const val STATUS_SUCCESS = "Selesai"
    const val STATUS_CANCEL = "Dibatalkan"

    // =======================================================
    // 4. UI TEXT & METADATA
    // Teks bawaan agar nama aplikasi konsisten di semua halaman
    // =======================================================
    const val APP_NAME = "ALBRK SHOESCARE"
    const val EMPTY_MESSAGE = "Belum ada data."
}