package com.example.agronet

data class Order(
    val orderId: Int = 0,
    val customerId: Int,
    val farmerId: Int,
    val totalAmount: Double,
    val items: List<CartItem>,
    val orderDate: Long
)
