package com.example.agronet


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