package com.example.agronet

data class Order(
    val orderId: Int,
    val customerId: Int,
    val orderDate: String,
    val customerName: String,
    val shippingAddress: String,
    val quantity: Double,
    val totalPrice: Double,
    val productImageResId: Int,
    val items: List<OrderItem>
)