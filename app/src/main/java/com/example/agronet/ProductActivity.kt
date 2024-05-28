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

        // Προσθήκη προϊόντων στη λίστα
        productList = listOf(
            Product(1,"Bananas Voivitias", "1.48€ / per kg", R.drawable.bananas, R.drawable.farmer_photo),
            Product(2,"Apples", "2.30€ / per kg", R.drawable.bananas, R.drawable.farmer_photo),
            Product(3,"Oranges", "1.20€ / per kg", R.drawable.bananas, R.drawable.farmer_photo),
            Product(4,"Grapes", "3.00€ / per kg", R.drawable.bananas, R.drawable.farmer_photo)
        )

        productAdapter = ProductAdapter(this, productList)
        productRecyclerView.adapter = productAdapter

    }
    }
