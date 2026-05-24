package com.albrk.shoescare.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.albrk.shoescare.data.firebase.model.ServiceItem
import com.albrk.shoescare.data.firebase.model.Transaction
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class ShoeViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance("https://albrk-shoescare-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val masterServicesRef = db.getReference("master_services")
    private val transactionsRef = db.getReference("transactions")
    private val usersRef = db.getReference("users")
    private val storage = FirebaseStorage.getInstance().reference

    // ==========================================
    // 1. STATE MANAGEMENT (DATA)
    // ==========================================
    private val _allServices = MutableStateFlow<List<ServiceItem>>(emptyList())
    val activeServices = _allServices.map { list -> list.filter { it.isActive } }

    // State KHUSUS untuk menyimpan riwayat pesanan milik pelanggan yang sedang login
    private val _myTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val myTransactions: StateFlow<List<Transaction>> = _myTransactions

    init {
        fetchServices()
    }

    // ==========================================
    // 2. FITUR KATALOG (BACA SAJA)
    // ==========================================
    private fun fetchServices() {
        masterServicesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<ServiceItem>()
                for (data in snapshot.children) {
                    val price = (data.child("price").value as? Long)?.toInt() ?: 0
                    list.add(ServiceItem(
                        id = data.key ?: "",
                        name = data.child("name").getValue(String::class.java) ?: "",
                        price = price,
                        imageUri = data.child("imageUri").getValue(String::class.java),
                        isActive = data.child("isActive").getValue(Boolean::class.java) ?: true
                    ))
                }
                _allServices.value = list
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // ==========================================
    // 3. FITUR TRACKING PESANAN (BARU)
    // ==========================================
    fun fetchMyTransactions(customerPhone: String) {
        if (customerPhone.isEmpty()) return

        // Cari transaksi yang nomor HP-nya sama dengan nomor HP pelanggan
        transactionsRef.orderByChild("customerPhone").equalTo(customerPhone)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Transaction>()
                    for (data in snapshot.children) {
                        data.getValue(Transaction::class.java)?.let {
                            list.add(it.copy(firebaseKey = data.key ?: ""))
                        }
                    }
                    _myTransactions.value = list.sortedByDescending { it.date }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // ==========================================
    // 4. MANAJEMEN PROFIL (TETAP AMAN)
    // ==========================================
    fun getUserProfile(uid: String, onResult: (String, String, String, String) -> Unit) {
        usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val phone = snapshot.child("phone").getValue(String::class.java) ?: ""
                val address = snapshot.child("address").getValue(String::class.java) ?: ""
                val photoUrl = snapshot.child("photoUrl").getValue(String::class.java) ?: ""
                onResult(name, phone, address, photoUrl)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun uploadProfileImage(uid: String, uri: Uri, onComplete: (String) -> Unit) {
        val fileRef = storage.child("profile_images/$uid.jpg")
        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                val url = downloadUrl.toString()
                usersRef.child(uid).child("photoUrl").setValue(url)
                onComplete(url)
            }
        }.addOnFailureListener {
            onComplete("")
        }
    }

    fun saveUserProfile(uid: String, name: String, phone: String, address: String, photoUrl: String, onSuccess: () -> Unit) {
        val userMap = mapOf(
            "name" to name,
            "phone" to phone,
            "address" to address,
            "photoUrl" to photoUrl
        )
        usersRef.child(uid).setValue(userMap).addOnSuccessListener { onSuccess() }
    }
}

// ==========================================
// FACTORY
// ==========================================
class ShoeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoeViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}