package com.clikzop.sales_standerd.Model


import com.google.gson.annotations.SerializedName

data class OfficeBreakStatusBean(
    @SerializedName("data")
    val `data`: Int, // 13
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String // Break Started successfully.
)