package com.albrk.shoescare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.albrk.shoescare.ui.theme.ALBRKSHOESCARETheme
import com.albrk.shoescare.ui.screen.auth.LoginScreen
import com.albrk.shoescare.ui.MyApp
import com.albrk.shoescare.viewmodel.ShoeViewModel
import com.albrk.shoescare.viewmodel.ShoeViewModelFactory

/**
 * MAIN ACTIVITY (ENTRY POINT - PELANGGAN)
 * Fungsi: Sebagai pondasi utama tempat Jetpack Compose menggambar seluruh antarmuka aplikasi.
 * Menangani pengecekan sesi login dan inisialisasi ViewModel utama.
 */
class MainActivity : ComponentActivity() {

    // =======================================================
    // 1. INISIALISASI VIEWMODEL (CLOUD-NATIVE)
    // =======================================================
    // Menggunakan Factory untuk membuat instance ViewModel yang terhubung ke Firebase.
    private val viewModel: ShoeViewModel by viewModels {
        ShoeViewModelFactory()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Membungkus aplikasi dengan tema warna sistem ALBRK
            ALBRKSHOESCARETheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // =======================================================
                    // 2. MANAJEMEN SESI PENGGUNA (REACTIVE STATE)
                    // =======================================================
                    // State ini menyimpan UID (User ID) unik dari Firebase Authentication.
                    // Jika null, aplikasi menampilkan Login. Jika terisi, masuk ke Dashboard.
                    var loggedInUserId by remember { mutableStateOf<String?>(null) }

                    if (loggedInUserId == null) {
                        // --- HALAMAN LOGIN & REGISTRASI ---
                        LoginScreen(
                            onLoginClick = { id ->
                                // Menyimpan UID saat berhasil masuk/daftar
                                loggedInUserId = id
                            }
                        )
                    } else {
                        // --- HALAMAN UTAMA APLIKASI (MY APP) ---
                        // PERBAIKAN: Sekarang mengirim 'uid' agar ProfileScreen bisa
                        // menarik data nama & alamat yang benar dari Firebase.
                        MyApp(
                            uid = loggedInUserId!!,
                            viewModel = viewModel,
                            onLogout = {
                                // Menghapus sesi untuk kembali ke layar Login
                                loggedInUserId = null
                            }
                        )
                    }
                }
            }
        }
    }
}