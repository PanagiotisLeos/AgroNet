package com.example.agronet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.AppCompatButton

class CheckoutFragment : Fragment() {

    private lateinit var cartItemsLayout: LinearLayout
    private lateinit var redeemPointsInput: EditText
    private lateinit var applyPointsButton: AppCompatButton
    private lateinit var totalPriceTextView: TextView
    private lateinit var confirmOrderButton: AppCompatButton
    private lateinit var tipSeekBar: SeekBar
    private lateinit var tipAmount: TextView

    private var totalAmount: Double = 0.0
    private var tipAmountValue: Double = 0.5 // initial tip amount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.checkout, container, false)

        cartItemsLayout = view.findViewById(R.id.cartItemsLayout)
        redeemPointsInput = view.findViewById(R.id.redeemPointsInput)
        applyPointsButton = view.findViewById(R.id.applyPointsButton)
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView)
        confirmOrderButton = view.findViewById(R.id.confirmOrderButton)
        tipSeekBar = view.findViewById(R.id.tipSeekBar)
        tipAmount = view.findViewById(R.id.tipAmount)

        displayCartItems()
        setupTipSeekBar()
        setupApplyPointsButton()
        setupConfirmOrderButton()

        return view
    }

    private fun displayCartItems() {
        val cartItems = CartManager.getItems()
        totalAmount = 0.0

        for (item in cartItems) {
            val itemView = layoutInflater.inflate(R.layout.cart_item, cartItemsLayout, false) as CardView

            val productImageView = itemView.findViewById<ImageView>(R.id.productImage)
            val productNameTextView = itemView.findViewById<TextView>(R.id.productName)
            val productQuantityTextView = itemView.findViewById<TextView>(R.id.productQuantity)
            val productPriceTextView = itemView.findViewById<TextView>(R.id.productPrice)

            productImageView.setImageResource(item.productImage)
            productNameTextView.text = item.productName
            productQuantityTextView.text = "Quantity: ${item.quantity} Kg"
            productPriceTextView.text = String.format("Total: €%.2f", item.totalPrice)

            totalAmount += item.totalPrice

            cartItemsLayout.addView(itemView)
        }

        updateTotalPrice()
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
        intent.putExtra("PRODUCT_PRICE", "€${item.totalPrice / item.quantity}")
        intent.putExtra("POSTED_BY_IMAGE", 0) // Προσθέστε το πραγματικό ID εικόνας αν είναι διαθέσιμο
        intent.putExtra("IS_EDITING", true)
        intent.putExtra("QUANTITY", item.quantity)
        startActivity(intent)
    }

    private fun setupTipSeekBar() {
        tipSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tipAmountValue = progress * 0.5
                tipAmount.text = String.format("€%.2f", tipAmountValue)
                updateTotalPrice()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupApplyPointsButton() {
        applyPointsButton.setOnClickListener {
            val points = redeemPointsInput.text.toString().toIntOrNull() ?: 0
            if (points > 0) {
                val discount = points * 0.1 // assuming each point gives €0.1 discount
                totalAmount -= discount
                Toast.makeText(context, "Applied €${discount} discount", Toast.LENGTH_SHORT).show()
                updateTotalPrice()
            } else {
                Toast.makeText(context, "Invalid points", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotalPrice() {
        val totalPrice = totalAmount + tipAmountValue
        totalPriceTextView.text = String.format("Total Price: €%.2f", totalPrice)
    }

    private fun setupConfirmOrderButton() {
        confirmOrderButton.setOnClickListener {
            Toast.makeText(context, "Order Confirmed!", Toast.LENGTH_SHORT).show()
            CartManager.clearCart()
            requireActivity().supportFragmentManager.popBackStack() // Close the checkout fragment
        }
    }
}
