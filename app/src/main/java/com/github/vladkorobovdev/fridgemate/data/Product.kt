package com.github.vladkorobovdev.fridgemate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val expirationDate: Long,
    val eanCode: String? = null,
    val imagePath: String? = null
)