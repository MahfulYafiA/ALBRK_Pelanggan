package com.albrk.shoescare.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.albrk.shoescare.data.firebase.model.ServiceItem
import com.albrk.shoescare.data.firebase.model.Transaction
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage // IMPORT WAJIB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class ShoeViewModel : ViewModel() {

    // 1. Inisialisasi Firebase
    private val db = FirebaseDatabase.getInstance("https://albrk-shoescare-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val transactionsRef = db.getReference("transactions")
    private val masterServicesRef = db.getReference("master_services")
    private val usersRef = db.getReference("users")

    // Inisialisasi Storage yang benar
    private val storage = FirebaseStorage.getInstance().reference

    // 2. State Management
    private val _allTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val allTransactions: StateFlow<List<Transaction>> = _allTransactions

    private val _allServices = MutableStateFlow<List<ServiceItem>>(emptyList())
    val allServices: StateFlow<List<ServiceItem>> = _allServices

    val activeServices = allServices.map { list -> list.filter { it.isActive } }

    init {
        fetchServices()
        fetchTransactions()
    }

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

    private fun fetchTransactions() {
        transactionsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Transaction>()
                for (data in snapshot.children) {
                    data.getValue(Transaction::class.java)?.let {
                        list.add(it.copy(firebaseKey = data.key ?: ""))
                    }
                }
                _allTransactions.value = list.sortedByDescending { it.date }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // --- MANAJEMEN PROFIL ---

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

    // --- TRANSAKSI ---
    fun submitBooking(customerName: String, customerPhone: String, address: String, serviceNames: String, totalPrice: Int) {
        val key = transactionsRef.push().key ?: return
        val newTransaction = Transaction(
            customerName = customerName,
            customerPhone = customerPhone,
            address = address,
            serviceNames = serviceNames,
            totalPrice = totalPrice,
            date = System.currentTimeMillis(),
            status = "Diajukan",
            firebaseKey = key
        )
        transactionsRef.child(key).setValue(newTransaction)
    }
}

// Perbaikan Factory
class ShoeViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShoeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShoeViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}