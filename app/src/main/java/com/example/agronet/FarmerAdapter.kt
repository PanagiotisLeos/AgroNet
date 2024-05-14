package com.example.agronet

import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FarmerAdapter(
    private var farmersList: List<Farmer>,
    private val onFarmerClick: (Farmer) -> Unit
) : RecyclerView.Adapter<FarmerAdapter.FarmerViewHolder>() {

    class FarmerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val farmerImg: ImageView = view.findViewById(R.id.farmer_image)
        private val fname: TextView = view.findViewById(R.id.farmer_name)
        private val lname: TextView = view.findViewById(R.id.farmer_last_name)
        private val location: TextView = view.findViewById(R.id.farm_location)
        private val farmType: TextView = view.findViewById(R.id.farm_type)

        fun bind(farmer: Farmer, clickListener: (Farmer) -> Unit) {
            fname.text = farmer.fname
            lname.text = farmer.lname
            location.text = farmer.location
            farmType.text = farmer.farmerType

            itemView.setOnClickListener { clickListener(farmer) }

            val context = itemView.context
            Glide.with(context)
                .load(farmer.profileImg)
                .placeholder(R.drawable.farmer)
                .error(R.drawable.gmail)
                .into(farmerImg)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FarmerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.farmer_item, parent, false)
        return FarmerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FarmerViewHolder, position: Int) {
        holder.bind(farmersList[position], onFarmerClick)
    }

    override fun getItemCount() = farmersList.size

    fun setData(newFarmers: List<Farmer>) {
        farmersList = newFarmers
        notifyDataSetChanged()
    }
}
