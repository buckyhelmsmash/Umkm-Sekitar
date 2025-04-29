package com.example.umkm_sekitar.data.source

import com.example.umkm_sekitar.data.model.Toko
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.jvm.java

class FirebaseDataSource @Inject constructor() {
    private val database = FirebaseDatabase.getInstance()
    private val tokoRef = database.getReference("toko")

    fun getAllToko(): Flow<List<Toko>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tokoList = mutableListOf<Toko>()
                for (tokoSnapshot in snapshot.children) {
                    val toko = tokoSnapshot.getValue(Toko::class.java)
                    toko?.let { tokoList.add(it) }
                }
                trySend(tokoList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        tokoRef.addValueEventListener(listener)

        awaitClose {
            tokoRef.removeEventListener(listener)
        }
    }

    fun getTokoByCategory(category: String): Flow<List<Toko>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tokoList = mutableListOf<Toko>()
                for (tokoSnapshot in snapshot.children) {
                    val toko = tokoSnapshot.getValue(Toko::class.java)
                    toko?.let {
                        if (it.kategori_toko.contains(category)) {
                            tokoList.add(it)
                        }
                    }
                }
                trySend(tokoList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        tokoRef.addValueEventListener(listener)

        awaitClose {
            tokoRef.removeEventListener(listener)
        }
    }

    fun getTokoByLocation(location: String): Flow<List<Toko>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tokoList = mutableListOf<Toko>()
                for (tokoSnapshot in snapshot.children) {
                    val toko = tokoSnapshot.getValue(Toko::class.java)
                    toko?.let {
                        if (it.lokasi.contains(location, ignoreCase = true)) {
                            tokoList.add(it)
                        }
                    }
                }
                trySend(tokoList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        tokoRef.addValueEventListener(listener)

        awaitClose {
            tokoRef.removeEventListener(listener)
        }
    }
}
