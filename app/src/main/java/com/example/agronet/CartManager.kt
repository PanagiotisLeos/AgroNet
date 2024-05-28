package com.example.agronet

object CartManager {
    private val cartItems = mutableListOf<CartItem>()

    fun addItem(item: CartItem) {
        cartItems.add(item)
    }

    fun getItems(): List<CartItem> {
        return cartItems
    }

    fun removeItem(item: CartItem) {
        cartItems.remove(item)
    }

    fun clearCart() {
        cartItems.clear()
    }
}
