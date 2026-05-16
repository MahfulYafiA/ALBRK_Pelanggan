package com.albrk.shoescare.ui.screen.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.albrk.shoescare.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(onLoginClick: (String) -> Unit) {
    // =======================================================
    // 1. STATE MANAGEMENT (MANAJEMEN STATUS UI)
    // =======================================================
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") } // Hanya muncul saat mode daftar
    var passwordVisible by remember { mutableStateOf(false) } // Toggle mata password

    // KUNCI UTAMA: Menentukan apakah layar menampilkan form Login (true) atau form Daftar (false)
    var isLoginMode by remember { mutableStateOf(true) }

    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // Indikator proses loading Firebase
    val context = LocalContext.current // Untuk menampilkan Toast (pesan pop-up)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEEE)) // Warna latar belakang abu-abu terang
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Memposisikan konten pas di tengah layar
    ) {
        // =======================================================
        // 2. HEADER: LOGO DAN JUDUL APLIKASI
        // =======================================================
        Surface(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape), // Memotong bingkai menjadi lingkaran sempurna
            color = Color.White,
            shadowElevation = 8.dp // Memberikan efek bayangan 3D
        ) {
            Image(
                painter = painterResource(id = R.drawable.albrk),
                contentDescription = "ALBRK Logo",
                contentScale = ContentScale.Crop, // Memastikan gambar mengisi lingkaran tanpa gepeng
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "ALBRK SHOESCARE",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        // Teks sub-judul yang berubah dinamis tergantung mode (Login / Daftar)
        Text(
            text = if (isLoginMode) "Silakan masuk ke akun Anda" else "Daftar akun baru sekarang",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // =======================================================
        // 3. INPUT FIELDS (KOLOM ISIAN)
        // =======================================================
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            singleLine = true,
            // Fitur untuk melihat atau menyembunyikan teks sandi
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(
                        text = if (passwordVisible) "Tutup" else "Lihat",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            // Mengubah teks menjadi titik-titik jika passwordVisible bernilai false
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        // --- FORM KONFIRMASI PASSWORD (CONDITIONAL RENDERING) ---
        // Hanya digambar oleh Jetpack Compose ke layar jika isLoginMode bernilai false
        if (!isLoginMode) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Konfirmasi Password", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation()
            )
        }

        // Area untuk menampilkan pesan error berwarna merah jika ada validasi yang gagal
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, color = Color.Red, fontSize = 14.sp)
        }

        // =======================================================
        // 4. TOMBOL UTAMA & LOGIKA FIREBASE AUTH
        // =======================================================
        Button(
            onClick = {
                // Validasi dasar (Front-end Validation)
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Email dan Password tidak boleh kosong!"
                    return@Button
                }

                isLoading = true // Memunculkan animasi muter (loading)
                errorMessage = ""

                val auth = FirebaseAuth.getInstance()

                if (isLoginMode) {
                    // --- LOGIKA LOGIN (SIGN IN) ---
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Login Berhasil", Toast.LENGTH_SHORT).show()
                                // Lanjut ke halaman berikutnya dengan membawa User ID (UID)
                                onLoginClick(auth.currentUser?.uid ?: "pelanggan")
                            } else {
                                errorMessage = "Login Gagal: Cek email dan password Anda."
                            }
                        }
                } else {
                    // --- LOGIKA DAFTAR (SIGN UP) ---
                    if (password != confirmPassword) {
                        isLoading = false
                        errorMessage = "Password tidak cocok!"
                        return@Button
                    }
                    if (password.length < 6) {
                        isLoading = false
                        errorMessage = "Password minimal 6 karakter!"
                        return@Button
                    }

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Akun berhasil dibuat!", Toast.LENGTH_SHORT).show()
                                // Otomatis mengarahkan user masuk ke dalam aplikasi setelah sukses daftar
                                onLoginClick(auth.currentUser?.uid ?: "pelanggan")
                            } else {
                                errorMessage = "Pendaftaran gagal: ${task.exception?.message}"
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading // Mematikan interaksi tombol saat sistem sedang memuat (loading)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = if (isLoginMode) "LOGIN" else "DAFTAR SEKARANG",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // =======================================================
        // 5. TEKS PINDAH MODE (TOGGLE)
        // =======================================================
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isLoginMode) "Belum punya akun? " else "Sudah punya akun? ",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Text(
                text = if (isLoginMode) "Daftar di sini" else "Login di sini",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    // KUNCI PERUBAHAN UI:
                    // Mengubah nilai isLoginMode akan memaksa Jetpack Compose menggambar ulang seluruh tampilan ini
                    isLoginMode = !isLoginMode
                    errorMessage = "" // Mereset pesan error agar tampilan bersih saat pindah mode
                }
            )
        }
    }
}