package com.example.agronet

data class OrderItem(
    val itemId: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val pricePerUnit: Double,
    val totalPrice: Double,
    val productImageResId: ByteArray?
)
