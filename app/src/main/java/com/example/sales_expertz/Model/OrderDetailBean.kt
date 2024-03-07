package com.example.sales_expertz.Model


import com.google.gson.annotations.SerializedName

data class OrderDetailBean(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String // Data loaded successfully.
) {
    data class Data(
        @SerializedName("order_det")
        val orderDet: List<OrderDet>,
        @SerializedName("order_mst")
        val orderMst: OrderMst
    ) {
        data class OrderDet(
            @SerializedName("category_name")
            val categoryName: String, // Bathing
            @SerializedName("color")
            val color: String, // White
            @SerializedName("id")
            val id: Int, // 45
            @SerializedName("price")
            val price: String, // 250.00
            @SerializedName("product_id")
            val productId: Int, // 1
            @SerializedName("product_name")
            val productName: String, // SENSOR FAUCET FOR WASH BASIN
            @SerializedName("qty")
            var qty: Int, // 30
            @SerializedName("ret_qty")
            val retQty: Int, // 0
            @SerializedName("total_amount")
            val totalAmount: String, // 7500.00
            @SerializedName("type")
            val type: String // box
        )

        data class OrderMst(
            @SerializedName("customer_id")
            val customerId: Int, // 7
            @SerializedName("customer_name")
            val customerName: String, // vaibhav
            @SerializedName("id")
            val id: Int, // 36
            @SerializedName("order_date")
            val orderDate: String, // 2024-03-01 18:47:26
            @SerializedName("order_value")
            val orderValue: Int, // 13500
            @SerializedName("status")
            val status: String // pending
        )
    }
}