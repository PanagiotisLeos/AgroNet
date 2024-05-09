package com.example.agronet

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class FarmerAdapter(private var farmersList: List<Farmer>) :
    RecyclerView.Adapter<FarmerAdapter.FarmerViewHolder>() {

    class FarmerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.farmer_image)
        val name: TextView = view.findViewById(R.id.farmer_name)
        val location: TextView = view.findViewById(R.id.farm_location)
        val farmType: TextView = view.findViewById(R.id.farm_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.farmer_item, parent, false)
        return FarmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        val farmer = farmersList[position]
        holder.name.text = farmer.name
        holder.location.text = farmer.location
        holder.imageView.setImageResource(farmer.imageId)
        holder.farmType.text = farmer.farmerType
        holder.imageView.setOnClickListener {
            val clickedColor = ContextCompat.getColor(holder.imageView.context, R.color.clicked_color)
            holder.imageView.setColorFilter(clickedColor, PorterDuff.Mode.SRC_IN)
        }
    }

    override fun getItemCount() = farmersList.size

    fun setData(newFarmers: List<Farmer>) {
        farmersList = newFarmers
        notifyDataSetChanged()
    }
}
