package com.example.agronet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private var orders: List<Order>,
    private val listener: OnOrderClickListener
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    interface OnOrderClickListener {
        fun onDeleteOrderClick(order: Order)
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val orderInfo: TextView = itemView.findViewById(R.id.order_info)
        val orderDate: TextView = itemView.findViewById(R.id.order_date)
        val orderId: TextView = itemView.findViewById(R.id.order_id)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_order_button)

        fun bind(order: Order) {
            productImage.setImageBitmap(byteArrayToBitmap(order.productImageResId))
            orderInfo.text = "Customer: ${order.customerName}\nShipping Address: ${order.shippingAddress}\nQuantity: ${order.quantity}\nTotal Price: €${order.totalPrice}"
            orderDate.text = "Order Date: ${order.orderDate}"
            orderId.text = "Order ID: ${order.orderId}"

            deleteButton.setOnClickListener {
                listener.onDeleteOrderClick(order)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount() = orders.size

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    private fun byteArrayToBitmap(byteArray: ByteArray?): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray?.size ?: 0)
    }
}
