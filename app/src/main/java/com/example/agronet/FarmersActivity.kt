package com.example.agronet

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agronet.R

class FarmersActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmers)

        val farmersList = listOf(
            Farmer("John Doe", "Mari Arkadias", R.drawable.login_icon, "Individual Farmer"),
            Farmer("Jane Doe", "location", R.drawable.login_icon,  "Individual Farmer"),
            Farmer("Jane Doe", "location", R.drawable.login_icon,  "Individual Farmer"),
            Farmer("Jane Doe", "location", R.drawable.login_icon,  "Individual Farmer"),
            Farmer("Jane Doe", "location", R.drawable.login_icon,  "Individual Farmer"),
            Farmer("Jane Doe", "location", R.drawable.login_icon,  "Individual Farmer"),
            Farmer("Jane Doe", "location", R.drawable.login_icon,  "Individual Farmer"),
            Farmer("Jane Doe", "location", R.drawable.login_icon,  "Individual Farmer"),
            Farmer("Jane Doe", "location", R.drawable.login_icon,  "Individual Farmer"),
            Farmer("Jane Doe", "location", R.drawable.login_icon,  "Individual Farmer"),
            Farmer("Jane Doe", "location", R.drawable.login_icon,  "Individual Farmer"),

            )

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_farmers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = FarmerAdapter(farmersList)



    }


}
