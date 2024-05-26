package com.example.agronet

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductPageAdapter(private val productContext: Context, private val productList: List<Product>) :
    RecyclerView.Adapter<ProductPageAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(productContext).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productName.text = product.name
        holder.productPrice.text = product.price
        holder.productImage.setImageResource(product.imageResId)
        holder.postedByImageView.setImageResource(product.postedByImageResId)

        holder.itemView.setOnClickListener {
            val intent = Intent(productContext, ProductDetailActivity::class.java).apply {
                putExtra("PRODUCT_NAME", product.name)
                putExtra("PRODUCT_IMAGE", product.imageResId)
                putExtra("PRODUCT_PRICE", product.price)
                putExtra("POSTED_BY_IMAGE", product.postedByImageResId)
            }
            productContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val postedByImageView: ImageView = itemView.findViewById(R.id.postedByImage)
    }
}
