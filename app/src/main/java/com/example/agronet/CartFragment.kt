package com.example.agronet

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.AppCompatButton
import java.io.ByteArrayOutputStream

class CartFragment : Fragment() {

    private lateinit var cartItemsLayout: LinearLayout
    private lateinit var checkoutButton: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        cartItemsLayout = view.findViewById(R.id.cartItemsLayout)
        checkoutButton = view.findViewById(R.id.checkoutButton)
        displayCartItems()
        setupCheckoutButton()
        return view
    }

    private fun displayCartItems() {
        val cartItems = CartManager.getItems()

        for (item in cartItems) {
            val itemView = layoutInflater.inflate(R.layout.cart_item, cartItemsLayout, false) as CardView

            val productImageView = itemView.findViewById<ImageView>(R.id.productImage)
            val productNameTextView = itemView.findViewById<TextView>(R.id.productName)
            val productQuantityTextView = itemView.findViewById<TextView>(R.id.productQuantity)
            val productPriceTextView = itemView.findViewById<TextView>(R.id.productPrice)
            val removeButton = itemView.findViewById<ImageButton>(R.id.removeButton)
            val editButton = itemView.findViewById<ImageButton>(R.id.editButton)

            productImageView.setImageBitmap(item.productImage?.let { byteArrayToBitmap(it) })
            productNameTextView.text = item.productName
            productQuantityTextView.text = "Quantity: ${item.quantity} Kg"
            productPriceTextView.text = String.format("Total: €%.2f", item.totalPrice)

            removeButton.setOnClickListener {
                removeItemFromCart(item)
            }

            editButton.setOnClickListener {
                editCartItem(item)
            }

            cartItemsLayout.addView(itemView)
        }
    }

    private fun removeItemFromCart(item: CartItem) {
        CartManager.removeItem(item)
        cartItemsLayout.removeAllViews()
        displayCartItems()
    }

    private fun editCartItem(item: CartItem) {
        val intent = Intent(context, ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_NAME", item.productName)
        intent.putExtra("PRODUCT_IMAGE", item.productImage)
        intent.putExtra("PRODUCT_PRICE", "€${item.price}")
        intent.putExtra("POSTED_BY_IMAGE", 0) // Add the actual image ID if available
        intent.putExtra("IS_EDITING", true)
        intent.putExtra("QUANTITY", item.quantity)
        intent.putExtra("PRODUCT_ID", item.productId)
        startActivity(intent)
    }

    private fun setupCheckoutButton() {
        checkoutButton.setOnClickListener {
            (activity as MainActivity).loadFragment(CheckoutFragment())
        }
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
