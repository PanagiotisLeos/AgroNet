package com.example.agronet

import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.security.AccessController.getContext

class FarmerAdapter(private var farmersList: List<Farmer>) :
    RecyclerView.Adapter<FarmerAdapter.FarmerViewHolder>() {

    class FarmerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val farmerImg: ImageView = view.findViewById(R.id.farmer_image)
        val fname: TextView = view.findViewById(R.id.farmer_name)
        val lname: TextView = view.findViewById(R.id.farmer_last_name)
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
        holder.fname.text = farmer.fname
        holder.lname.text = farmer.lname
        holder.location.text = farmer.location
        holder.farmType.text = farmer.farmerType

        val context = holder.itemView.context

        val profileImg = farmer.getprofile_img()
        if (profileImg != null) {
            Log.d("FarmerAdapter", "Profile Image Length: ${profileImg.size}")
        } else {
            Log.d("FarmerAdapter", "Profile Image is null")
        }

        Glide.with(context)
            .load(profileImg)
            .placeholder(R.drawable.farmer) // optional placeholder image
            .error(R.drawable.gmail) // optional error image
            .into(holder.farmerImg)
    }


    override fun getItemCount() = farmersList.size

    fun setData(newFarmers: List<Farmer>) {
        farmersList = newFarmers
        notifyDataSetChanged()
    }
}
