package com.example.test.module

import java.io.Serializable

data class Cart(
    var productId: String,
    var productName: String,
    var productPrice: String,
    var productImage: String,
    var productQty: String,
    var checked: Boolean = false
) : Serializable{}