package com.clikzop.sales_standerd.Model


import com.google.gson.annotations.SerializedName

data class DashboardBean(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: Boolean, // false
    @SerializedName("msg")
    val msg: String // Dataloaded successfully.
) {
    data class Data(
        @SerializedName("approved_order")
        val approvedOrder: String, // 1
        @SerializedName("area_report")
        val areaReport: String, // null
        @SerializedName("delivered_order")
        val deliveredOrder: String, // 3
        @SerializedName("dispatched_order")
        val dispatchedOrder: String, // 0
        @SerializedName("last_month_expense")
        val lastMonthExpense: String, // 0
        @SerializedName("pending_order")
        val pendingOrder: String, // 2
        @SerializedName("rejected_order")
        val rejectedOrder: String, // 1
        @SerializedName("this_month_expense")
        val thisMonthExpense: String, // 250
        @SerializedName("office_break_status")
        val officeBreakStatus: Boolean,
        @SerializedName("office_break_data")
        val officeBreakData: OfficeBreakData
    )
    {
        data class OfficeBreakData(
            @SerializedName("id")
            val id: Int,
            @SerializedName("start_time")
            val startTime: String,
            @SerializedName("stop_time")
            val stopTime: Any,
            @SerializedName("user_id")
            val userId: Int
        )
    }
}