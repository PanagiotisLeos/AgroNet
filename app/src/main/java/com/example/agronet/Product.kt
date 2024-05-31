package com.example.agronet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val farmerId: Int,
    val imageResId: Bitmap?,
    val postedByImageResId: Bitmap?
) {
         fun createProduct(
            context: Context,
            id: Int,
            name: String,
            price: String,
            farmerId: Int,
            imageResId: Bitmap?,
            postedByImageResId: Bitmap?
        ): Product {
            val defaultProductImage = BitmapFactory.decodeResource(context.resources, R.drawable.bananas)
            val defaultFarmerImage = BitmapFactory.decodeResource(context.resources, R.drawable.farmer_photo)

            return Product(
                id,
                name,
                price,
                farmerId,
                imageResId ?: defaultProductImage,
                postedByImageResId ?: defaultFarmerImage
            )
        }
}
