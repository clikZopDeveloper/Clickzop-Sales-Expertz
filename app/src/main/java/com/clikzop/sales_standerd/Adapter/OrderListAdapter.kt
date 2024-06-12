package com.clikzop.sales_standerd.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clikzop.sales_standerd.R
import com.clikzop.sales_standerd.Model.OrderListBean

import com.clikzop.sales_standerd.Utills.RvStatusComplClickListner
import com.stpl.antimatter.Utils.ApiContants


class OrderListAdapter(
    var context: Activity,
    var list: List<OrderListBean.Data>,
    var rvClickListner: RvStatusComplClickListner
) : RecyclerView.Adapter<OrderListAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder { // infalte the item Layout
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_list, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)

        /*     holder.tvAdd.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(20f))
             holder.tvQtyAdd.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(100f))
             holder.tvQtyMinus.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(100f))
             holder.tvQty.background = RoundView(Color.TRANSPARENT, RoundView.getRadius(20f), true, R.color.orange)
             holder.tvOff.background = RoundView(context.resources.getColor(R.color.orange), RoundView.getRadius(20f))
             holder.tvAdd.visibility = View.VISIBLE*/


        holder.tvCustomerName.text = list[position].customerName
        holder.tvPrice.text = ApiContants.currency+list[position].orderValue.toString()
        holder.tvDate.text = list[position].orderDate.toString()


        holder.itemView.setOnClickListener {
            rvClickListner.clickPos("", "", "", list[position].id)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tvCustomerName: TextView = itemview.findViewById(R.id.tvCustomerName)
        val tvPrice: TextView = itemview.findViewById(R.id.tvPrice)
        val tvDate: TextView = itemview.findViewById(R.id.tvDate)

    }
}