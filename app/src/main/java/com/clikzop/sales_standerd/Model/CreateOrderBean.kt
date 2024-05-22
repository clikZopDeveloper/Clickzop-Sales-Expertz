package com.clikzop.sales_standerd.Model


import com.google.gson.annotations.SerializedName

data class CreateOrderBean(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String // Return Order created sucessfully.
) {
    data class Data(
        @SerializedName("order_id")
        val orderId: String, // 2
        @SerializedName("order_total")
        val orderTotal: Int, // 66
        @SerializedName("product_count")
        val productCount: Int // 1
    )
}