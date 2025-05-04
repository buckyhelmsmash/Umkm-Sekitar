package com.example.umkm_sekitar.data.source

import android.util.Log
import com.example.umkm_sekitar.data.model.CartItem
import com.example.umkm_sekitar.data.model.CheckoutOrder
import com.example.umkm_sekitar.data.model.Product
import com.example.umkm_sekitar.data.model.Store
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.java

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

    fun getStoreById(storeId: String): Flow<Store?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var result: Store? = null
                for (child in snapshot.children) {
                    val store = child.getValue(Store::class.java)
                    if (store?.id == storeId) {
                        result = store
                        break
                    }
                }
                trySend(result).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        storeRef.addListenerForSingleValueEvent(listener)

        awaitClose {
            storeRef.removeEventListener(listener)
        }
    }

    fun addToCart(userId: String, storeId: String, productId: String, quantity: Int): Flow<Boolean> =
        callbackFlow {
            val listRef = storeRef.root.child("cart").child(userId)

            listRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val cartList = snapshot.getValue(object : GenericTypeIndicator<MutableList<CartItem>>() {}) ?: mutableListOf()

                    val existingItemIndex = cartList.indexOfFirst { it.productId == productId }

                    if (existingItemIndex != -1) {
                        val existingItem = cartList[existingItemIndex]
                        val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
                        cartList[existingItemIndex] = updatedItem
                    } else {
                        val newItem = CartItem(
                            storeId = storeId,
                            productId = productId,
                            quantity = quantity
                        )
                        cartList.add(newItem)
                    }

                    listRef.setValue(cartList)
                        .addOnSuccessListener { trySend(true) }
                        .addOnFailureListener { e ->
                            trySend(false)
                            close(e)
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(false)
                    close(error.toException())
                }
            })

            awaitClose { }
        }


    fun getCartItems(userId: String): Flow<List<CartItem>> = callbackFlow {
        val cartRef = storeRef.root.child("cart").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CartItem>()
                for (child in snapshot.children) {
                    val item = child.getValue(CartItem::class.java)
                    item?.let { list.add(it) }
                    Log.d("Firebase", "Children count: ${child.childrenCount}")

                }
                trySend(list).isSuccess
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        cartRef.addValueEventListener(listener)
        awaitClose { cartRef.removeEventListener(listener) }
    }

    fun getProductById(storeId: String, productId: String): Flow<Product?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var foundProduct: Product? = null

                for (storeSnap in snapshot.children) {
                    val store = storeSnap.getValue(Store::class.java)
                    if (store?.id == storeId) {
                        foundProduct = store.products.find { it.id == productId }
                        break
                    }
                }
                trySend(foundProduct).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        storeRef.addListenerForSingleValueEvent(listener)
        awaitClose { storeRef.removeEventListener(listener) }
    }

    fun saveCheckoutOrder(checkoutOrder: CheckoutOrder): Flow<Boolean> = callbackFlow {
        val listRef = storeRef.root.child("checkout_orders").child(checkoutOrder.userId)

        listRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = snapshot.getValue(object : GenericTypeIndicator<MutableList<CheckoutOrder>>() {}) ?: mutableListOf()

                orderList.add(checkoutOrder)

                listRef.setValue(orderList)
                    .addOnSuccessListener { trySend(true) }
                    .addOnFailureListener { e ->
                        trySend(false)
                        close(e)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(false)
                close(error.toException())
            }
        })

        awaitClose { }
    }

    fun updateCheckoutOrderStatus(userId: String, orderId: String, newStatus: String): Flow<Boolean> = callbackFlow {
        val listRef = storeRef.root.child("checkout_orders").child(userId)

        listRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = snapshot.getValue(object : GenericTypeIndicator<MutableList<CheckoutOrder>>() {}) ?: mutableListOf()

                val index = orderList.indexOfFirst { it.orderId == orderId }
                if (index != -1) {
                    val updatedOrder = orderList[index].copy(status = newStatus)
                    orderList[index] = updatedOrder

                    listRef.setValue(orderList)
                        .addOnSuccessListener { trySend(true) }
                        .addOnFailureListener { e ->
                            trySend(false)
                            close(e)
                        }
                } else {
                    trySend(false)
                    close(Throwable("Order ID not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(false)
                close(error.toException())
            }
        })

        awaitClose { }
    }



    fun getCheckoutOrdersByUserId(userId: String): Flow<List<CheckoutOrder>> = callbackFlow {
        val checkoutOrdersRef = storeRef.root.child("checkout_orders").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val checkoutOrders = mutableListOf<CheckoutOrder>()
                for (checkoutSnapshot in snapshot.children) {
                    val checkoutOrder = checkoutSnapshot.getValue(CheckoutOrder::class.java)
                    checkoutOrder?.let { checkoutOrders.add(it) }
                }
                trySend(checkoutOrders)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        checkoutOrdersRef.addValueEventListener(listener)

        awaitClose {
            checkoutOrdersRef.removeEventListener(listener)
        }
    }

    fun updateCartItemChecked(userId: String, productId: String, isChecked: Boolean): Flow<Boolean> = callbackFlow {
        val cartRef = storeRef.root.child("cart").child(userId)

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val updatedList = mutableListOf<CartItem>()
                for (child in snapshot.children) {
                    val item = child.getValue(CartItem::class.java)
                    if (item != null) {
                        if (item.productId == productId) {
                            updatedList.add(item.copy(checked = isChecked))
                        } else {
                            updatedList.add(item)
                        }
                    }
                }
                cartRef.setValue(updatedList)
                    .addOnSuccessListener {
                        trySend(true)
                    }
                    .addOnFailureListener { e ->
                        trySend(false)
                        close(e)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(false)
                close(error.toException())
            }
        })

        awaitClose { }
    }





}


