package com.example.agronet

import android.content.Context
import androidx.core.text.trimmedLength

class Cart(context: Context) {

    private val products = mutableListOf<Product>()
    private val dbManager = DatabaseManager()

    fun addProduct(product: Product) {
        products.add(product)
    }

    fun removeProduct(product: Product) {
        products.remove(product)
    }

    fun getProducts(): List<Product> {
        return products.toList()
    }

    fun getTotalPrice(): Double {
        return products.sumOf { it.price.substring(6).toDouble() }
    }

    fun clearCart() {
        products.clear()
    }

    fun fetchProductsFromDatabase() {
        val dbProducts = dbManager.getAllProducts()
        products.clear()
        products.addAll(dbProducts)
    }

    override fun toString(): String {
        return products.joinToString(separator = "\n") { "${it.name}: ${it.price}€" }
    }
}