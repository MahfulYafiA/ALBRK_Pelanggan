plugins {
    // Gunakan alias (Version Catalog) agar sinkron dengan file libs.versions.toml
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)

    // Plugin Google Services wajib untuk Firebase
    id("com.google.gms.google-services")
}

android {
    namespace = "com.albrk.shoescare"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.albrk.shoescare.pelanggan"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // =======================================================
    // FIREBASE STACK (KUNCI UTAMA)
    // =======================================================
    // Menggunakan Firebase BoM untuk manajemen versi otomatis yang stabil
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Database untuk simpan data teks (Transaksi & Profil)
    implementation("com.google.firebase:firebase-database")

    // Auth untuk Login, Daftar, Update Email & Sandi
    implementation("com.google.firebase:firebase-auth-ktx")

    // [PENTING] Storage untuk simpan file foto profil pelanggan
    implementation("com.google.firebase:firebase-storage-ktx")

    // =======================================================
    // JETPACK COMPOSE & CORE
    // =======================================================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // =======================================================
    // LIBRARY TAMBAHAN (UI & IMAGE)
    // =======================================================
    // [WAJIB] Coil untuk menampilkan foto profil dari URL Firebase Storage
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Room (Opsional: Tetap ada jika ingin pengembangan offline di masa depan)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
}