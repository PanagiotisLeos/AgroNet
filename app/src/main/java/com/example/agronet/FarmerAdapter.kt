package com.example.agronet

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FarmerAdapter(
    private var farmersList: List<Farmer>,
    private val listener: OnFarmerClickListener
) : RecyclerView.Adapter<FarmerAdapter.FarmerViewHolder>() {

    class FarmerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val farmerImg: ImageView = view.findViewById(R.id.farmer_image)
        val fname: TextView = view.findViewById(R.id.farmer_name)
        val lname: TextView = view.findViewById(R.id.farmer_last_name)
        val location: TextView = view.findViewById(R.id.farm_location)
        val farmType: TextView = view.findViewById(R.id.farm_type)
        val star: ImageView = view.findViewById(R.id.star)
        val totalStars: TextView = view.findViewById(R.id.totalStars)
        val rootView = view
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
        holder.totalStars.text = farmer.total_stars.toString()

        if (farmer.profileImg !== null) {
            val bitmap = BitmapFactory.decodeByteArray(farmer.profileImg, 0, farmer.profile_img.size)
            holder.farmerImg.setImageBitmap(bitmap)
        }
        else holder.farmerImg.setImageResource(R.drawable.farmer_photo)

        holder.rootView.setOnClickListener {
            listener.onFarmerClick(farmer.id)
        }

        if (farmer.isCustomerHasStarred) {
            holder.star.setImageResource(R.drawable.ic_star_gold);
        } else {
            holder.star.setImageResource(R.drawable.ic_star);
        }

    }


    override fun getItemCount() = farmersList.size

    fun setData(newFarmers: List<Farmer>) {
        farmersList = newFarmers
        notifyDataSetChanged()
    }
}
