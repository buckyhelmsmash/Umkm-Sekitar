package com.example.umkm_sekitar.data.source
//
import com.example.umkm_sekitar.data.model.Store
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.jvm.java
//
class FirebaseDataSource @Inject constructor(
    private val storeRef: DatabaseReference
) {
    fun getAllStore(): Flow<List<Store>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val storeList = mutableListOf<Store>()
                for (storeSnapshot in snapshot.children) {
                    val store = storeSnapshot.getValue(Store::class.java)
                    store?.let { storeList.add(it) }
                }
                trySend(storeList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        storeRef.addValueEventListener(listener)

        awaitClose {
            storeRef.removeEventListener(listener)
        }
    }

    fun getStoreByCategory(category: String): Flow<List<Store>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val storeList = mutableListOf<Store>()
                for (storeSnapshot in snapshot.children) {
                    val store = storeSnapshot.getValue(Store::class.java)
                    store?.let {
                        if (it.category.contains(category)) {
                            storeList.add(it)
                        }
                    }
                }
                trySend(storeList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        storeRef.addValueEventListener(listener)

        awaitClose {
            storeRef.removeEventListener(listener)
        }
    }

    fun getStoreByLocation(location: String): Flow<List<Store>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val storeList = mutableListOf<Store>()
                for (storeSnapshot in snapshot.children) {
                    val store = storeSnapshot.getValue(Store::class.java)
                    store?.let {
                        if (it.location.contains(location, ignoreCase = true)) {
                            storeList.add(it)
                        }
                    }
                }
                trySend(storeList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        storeRef.addValueEventListener(listener)

        awaitClose {
            storeRef.removeEventListener(listener)
        }
    }
}


