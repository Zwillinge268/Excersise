package com.example.test.module

import java.io.Serializable

data class Product(
    var productId: String,
    var productImage: String,
    var productName: String,
    var productPrice: String,
    var productContent: String,
    var productType: String,
    var productDetailImage: String
) : Serializable {}