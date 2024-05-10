package com.example.agronet

// AddProductActivity.kt


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddProductActivity : AppCompatActivity() {

    private lateinit var productImage: ImageView
    private lateinit var addPhotoButton: ImageButton
    private lateinit var productNameInput: EditText
    private lateinit var productPriceInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product)

        productImage = findViewById(R.id.productImage)
        addPhotoButton = findViewById(R.id.addPhotoButton)
        productNameInput = findViewById(R.id.productNameInput)
        productPriceInput = findViewById(R.id.productPriceInput)
        submitButton = findViewById(R.id.submitButton)

        addPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        submitButton.setOnClickListener {
            val productName = productNameInput.text.toString()
            val productPrice = productPriceInput.text.toString().toDoubleOrNull()

            if (productName.isNotEmpty() && productPrice != null) {
                // Handle product submission
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()

                // Redirect to FarmerProfileActivity
                val intent = Intent(this, FarmerProfileActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Show error message
                Toast.makeText(this, "Please fill out all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            productImage.setImageURI(imageUri)
        }
    }
}

