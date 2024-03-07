package com.example.sales_expertz.Adapter

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sales_expertz.Model.MultipleProductBean
import com.example.sales_expertz.Model.OrderDetailBean
import com.example.sales_expertz.R
import com.example.sales_expertz.Utills.RvCreateOrderClickListner
import com.google.gson.Gson
import com.stpl.antimatter.Utils.ApiContants


class OderDetailListAdapter(var context: Activity, var list: List<OrderDetailBean.Data.OrderDet>, var rvClickListner: RvCreateOrderClickListner) : RecyclerView.Adapter<OderDetailListAdapter.MyViewHolder>(){
    private var multiple: MultipleProductBean?=null
    val listw: MutableList<MultipleProductBean> = ArrayList()

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

      //  val currentItem = list[position]
        holder.tvPName.text= list.get(position).productName.toString()
        holder.tvType.text= "By "+list.get(position).type.toString()
        holder.tvColor.text= list.get(position).color.toString()
        holder.tvCatName.text= list.get(position).categoryName.toString()
        holder.tvQty.setText(list.get(position).qty.toString())
        if (!list.get(position).retQty.toString().isNullOrEmpty()){
            holder.tvReturnQty.setText("Return Qty : "+list.get(position).retQty.toString())
        }

        holder.tvPrice.text=ApiContants.currency+ list.get(position).price.toString()
        holder.tvTotalAmount.text= ApiContants.currency+list.get(position).totalAmount.toString()

//holder.tvQty.isEnabled=true
        /* multiple = MultipleProductBean(
             list.get(position).productName.toString(),
             list.get(position).price.toString(),
             list.get(position).productId.toString(),
             list.get(position).qty.toString(), "")*/

       // listw.clear()
     //   listw.add(multiple!!)
       // Log.d("vbnvbn",Gson().toJson(listw))

        holder.tvQty.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s!=null) {
                    list.get(position).qty = s.toString().toIntOrNull() ?: 0
                   /* multiple = MultipleProductBean(
                        list.get(position).productName.toString(),
                        list.get(position).price.toString(),
                        list.get(position).productId.toString(),
                        list.get(position).qty.toString(), "")*/
                }
             //   listw.add(multiple!!)
              //  Log.d("vbnvbn",Gson().toJson(listw))
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

      //  Log.d("vbnvbn",Gson().toJson(multiple!!))

    //    listmm.addAll(listw)
       //     Log.d("vbnvbn",Gson().toJson(listw))
     //   Log.d("vbnvbn",Gson().toJson(listmm))

        holder.itemView.setOnClickListener {
          //  rvClickListner.clickPos(listw,list[position].id)
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