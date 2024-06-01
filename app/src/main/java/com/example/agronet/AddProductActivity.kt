package com.example.agronet

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.sql.Connection
import java.sql.PreparedStatement

class AddProductActivity : AppCompatActivity() {

    private lateinit var productImage: ImageView
    private lateinit var addPhotoButton: ImageButton
    private lateinit var productNameInput: EditText
    private lateinit var productPriceInput: EditText
    private lateinit var submitButton: Button
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product)

        productImage = findViewById(R.id.productImage)
        addPhotoButton = findViewById(R.id.addPhotoButton)
        productNameInput = findViewById(R.id.productNameInput)
        productPriceInput = findViewById(R.id.productPriceInput)
        submitButton = findViewById(R.id.submitButton)
        sessionManager = SessionManager(this)

        addPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        submitButton.setOnClickListener {
            val productName = productNameInput.text.toString()
            val productPrice = productPriceInput.text.toString().toDoubleOrNull()
            val farmerId = sessionManager.userId.toInt()
            val prodImage = getImageByteArray(productImage)

            if (productName.isNotEmpty() && productPrice != null) {
                Log.d("AddProductActivity", "Product name: $productName, Product price: $productPrice")

                val product = Product(0, productName, productPrice.toString(), farmerId, byteArrayToBitmap(prodImage), byteArrayToBitmap(prodImage))

                // Upload the product to the database
                GlobalScope.launch(Dispatchers.IO) {
                    val success = uploadProductToDatabase(product, prodImage)
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Log.d("AddProductActivity", "Product added successfully")
                            Toast.makeText(this@AddProductActivity, "Product added successfully", Toast.LENGTH_SHORT).show()

                            finish()
                        } else {
                            Log.e("AddProductActivity", "Failed to add product")
                            Toast.makeText(this@AddProductActivity, "Failed to add product", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Log.e("AddProductActivity", "Please fill out all fields correctly")
                // Show error message
                Toast.makeText(this, "Please fill out all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    private fun getImageByteArray(imageView: ImageView): ByteArray {
        val drawable = imageView.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val inputStream = contentResolver.openInputStream(imageUri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val resizedBitmap = resizeBitmap(bitmap, 300,300) // Resize to desired max length, e.g., 500 pixels
            productImage.setImageBitmap(resizedBitmap)
            Log.d("AddProductActivity", "Image selected and resized: $imageUri")
        }
    }

    private fun uploadProductToDatabase(product: Product, prodImage: ByteArray): Boolean {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        return try {
            Log.d("AddProductActivity", "Connecting to database...")
            connection = DatabaseManager.getConnection()

            val sql = "INSERT INTO product (name, description, price, farmer_id, prod_image, created_at) VALUES (?, ?, ?, ?, ?, now())"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, product.name)
            preparedStatement.setString(2, "name")
            preparedStatement.setDouble(3, product.price.toDouble())
            preparedStatement.setInt(4, product.farmerId)
            preparedStatement.setBytes(5, prodImage)

            // Execute the statement
            val rowsAffected = preparedStatement.executeUpdate()
            Log.d("AddProductActivity", "Rows affected: $rowsAffected")
            rowsAffected > 0
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("AddProductActivity", "Database error: ${e.message}")
            false
        } finally {
            preparedStatement?.close()
            connection?.close()
            Log.d("AddProductActivity", "Database connection closed")
        }
    }

    // Utility function to resize bitmap
    private fun resizeBitmap(source: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true)
    }
}
