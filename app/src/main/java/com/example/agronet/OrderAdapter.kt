package com.example.agronet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private val orders: List<Order>,
    private val listener: OnOrderClickListener
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    interface OnOrderClickListener {
        fun onDeleteOrderClick(order: Order)
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productId: TextView = itemView.findViewById(R.id.product_id)
        val orderInfo: TextView = itemView.findViewById(R.id.order_info)
        val orderDate: TextView = itemView.findViewById(R.id.order_date)
        val orderId: TextView = itemView.findViewById(R.id.order_id)
        val deleteButton: ImageButton = itemView.findViewById(R.id.delete_order_button)

        fun bind(order: Order) {
            productImage.setImageResource(order.productImageResId)
            productId.text = "Product ID: ${order.orderId}"
            orderInfo.text = "Customer: ${order.customerName}\nShipping Address: ${order.shippingAddress}\nQuantity: ${order.quantity}\nTotal Price: ${order.totalPrice}"
            orderDate.text = order.orderDate
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
}
