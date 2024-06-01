package com.example.agronet

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

object OrderManager {
    suspend fun insertOrder(
        customerId: Int,
        farmerId: Int,
        totalPrice: Double,
        items: List<CartItem>
    ) {
        withContext(Dispatchers.IO) {
            val connection: Connection = DatabaseManager.getConnection()
            try {
                connection.autoCommit = false

                val orderQuery =
                    "INSERT INTO orders (customer_id, farmer_id, status, total_price, created_at) VALUES (?, ?, 'pending', ?, NOW())"
                val orderStmt: PreparedStatement =
                    connection.prepareStatement(orderQuery, PreparedStatement.RETURN_GENERATED_KEYS)
                orderStmt.setInt(1, customerId)
                orderStmt.setInt(2, farmerId)
                orderStmt.setDouble(3, totalPrice)
                orderStmt.executeUpdate()

                val rs: ResultSet = orderStmt.generatedKeys
                rs.next()
                val orderId = rs.getInt(1)

                val itemQuery =
                    "INSERT INTO order_items (order_id, product_id, quantity, price, total) VALUES (?, ?, ?, ?, ?)"
                val itemStmt: PreparedStatement = connection.prepareStatement(itemQuery)

                for (item in items) {
                    itemStmt.setInt(1, orderId)
                    itemStmt.setInt(2, item.productId)
                    itemStmt.setDouble(3, item.quantity)
                    itemStmt.setDouble(4, item.price)
                    itemStmt.setDouble(5, item.totalPrice)
                    itemStmt.addBatch()
                }

                itemStmt.executeBatch()
                connection.commit()
            } catch (e: SQLException) {
                connection.rollback()
                throw e
            } finally {
                connection.autoCommit = true
                connection.close()
            }
        }
    }
}
