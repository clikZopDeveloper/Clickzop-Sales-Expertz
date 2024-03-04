package com.example.sales_expertz.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sales_expertz.Model.CustomerDetailBean
import com.example.sales_expertz.Model.MultipleProductBean
import com.example.sales_expertz.Model.OrderDetailBean
import com.example.sales_expertz.R
import com.example.sales_expertz.Utills.RvCreateOrderClickListner
import com.example.sales_expertz.Utills.RvListClickListner
import com.example.sales_expertz.Utills.RvStatusClickListner
import com.stpl.antimatter.Utils.ApiContants


class OderDetailListAdapter(var context: Activity, var list: List<OrderDetailBean.Data.OrderDet>, var rvClickListner: RvCreateOrderClickListner) : RecyclerView.Adapter<OderDetailListAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder { // infalte the item Layout
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_order_detail_list, parent, false)
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

        holder.tvPName.text= list[position].productName.toString()
        holder.tvType.text= "By "+list[position].type.toString()
        holder.tvColor.text= list[position].color.toString()
        holder.tvCatName.text= list[position].categoryName.toString()
        holder.tvQty.setText(list[position].qty.toString())
        if (!list[position].retQty.toString().isNullOrEmpty()){
            holder.tvReturnQty.setText("Return Qty : "+list[position].retQty.toString())
        }

        holder.tvPrice.text=ApiContants.currency+ list[position].price.toString()
        holder.tvTotalAmount.text= ApiContants.currency+list[position].totalAmount.toString()
//holder.tvQty.isEnabled=true
        holder.itemView.setOnClickListener {
          //  rvClickListner.clickPos(list[position].status,list[position].id)
        }
        val multiple = MultipleProductBean(
            list[position].productName.toString(),
            list[position].price.toString(),
            list[position].productId.toString(),
            holder.tvQty.text.toString(), ""
        )

        holder.tvUpdateQty.setOnClickListener {
            rvClickListner.clickPos(multiple,list[position].id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tvPName: TextView = itemview.findViewById(R.id.tvPName)
        val tvType: TextView = itemview.findViewById(R.id.tvType)
        val tvColor: TextView = itemview.findViewById(R.id.tvColor)
        val tvCatName: TextView = itemview.findViewById(R.id.tvCatName)
        val tvQty: EditText = itemview.findViewById(R.id.tvQty)
        val tvPrice: TextView = itemview.findViewById(R.id.tvPrice)
        val tvReturnQty: TextView = itemview.findViewById(R.id.tvReturnQty)
        val tvUpdateQty: TextView = itemview.findViewById(R.id.tvUpdateQty)
        val tvTotalAmount: TextView = itemview.findViewById(R.id.tvTotalAmount)

    }
}