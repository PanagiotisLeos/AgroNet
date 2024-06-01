package com.example.agronet

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductActivity : AppCompatActivity() {

    private lateinit var productRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: List<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.farmer_profile) // Βεβαιωθείτε ότι καλεί το σωστό αρχείο XML

        // Βρείτε το RecyclerView χρησιμοποιώντας το ID του
        productRecyclerView = findViewById(R.id.productRecyclerView)
        productRecyclerView.layoutManager = GridLayoutManager(this, 3)



        productAdapter = ProductAdapter(this, productList)
        productRecyclerView.adapter = productAdapter

    }
    }
