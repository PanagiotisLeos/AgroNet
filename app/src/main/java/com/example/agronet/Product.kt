package com.example.agronet

data class Product(
    val id: Int ,
    val name: String,
    val price: String,
    val farmerId: Int,
    val imageResId: Int,
    val postedByImageResId: Int
)
