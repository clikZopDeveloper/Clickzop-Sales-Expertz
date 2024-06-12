package com.clikzop.sales_standerd.Adapter

import android.app.Activity
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.clikzop.sales_standerd.Model.ContactListBean
import com.clikzop.sales_standerd.Model.MettingListBean

import com.clikzop.sales_standerd.Utills.RvStatusComplClickListner


class MettingListAdapter(
    var context: Activity,
    var list: List<MettingListBean.Data>,
    var rvClickListner: RvStatusComplClickListner
) : RecyclerView.Adapter<MettingListAdapter.MyViewHolder>(), Filterable {
    var mFilteredList: MutableList<MettingListBean.Data> = list as MutableList<MettingListBean.Data>
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder { // infalte the item Layout
        val v =
            LayoutInflater.from(parent.context).inflate(com.clikzop.sales_standerd.R.layout.item_metting_list, parent, false)
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


        holder.tvClientName.text = mFilteredList[position].clientName
        holder.tvMobile.text = mFilteredList[position].clientContact
        holder.tvAddress.text = mFilteredList[position].clientAddress.toString()
        holder.tvContactPerson.text = mFilteredList[position].contactPerson
        holder.tvStartTime.text = mFilteredList[position].startTime.toString()
        holder.tvStopTime.text = mFilteredList[position].stopTime?.toString()
        holder.tvStartLocation.text = mFilteredList[position].startLocation.toString()
        holder.tvStopLocation.text = mFilteredList[position].stopLocation?.toString()
        holder.tvRemark.text = mFilteredList[position].remarks?.toString()


        if (mFilteredList[position].stopTime != null || !mFilteredList[position].stopTime.isNullOrEmpty()) {
        //    val shapeDrawable =ShapeDrawable
           // shapeDrawable.paint.color = ContextCompat.getColor(context, com.example.sales_expertz.R.color.colorPrimary)
            holder.tvStopMeeting.background.setColorFilter(Color.parseColor("#008000"), PorterDuff.Mode.SRC_ATOP)
            holder.tvStopMeeting.setText("Meeting Over")

        } else {
            holder.tvStopMeeting.background.setColorFilter(Color.parseColor("#b90609"), PorterDuff.Mode.SRC_ATOP)
            holder.tvStopMeeting.setText("Stop Meeting")
            holder.tvStopMeeting.setOnClickListener {
                rvClickListner.clickPos("", "", "", mFilteredList[position].id)
            }
        }


    }

    override fun getItemCount(): Int {
        return mFilteredList.size
    }

    inner class MyViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val tvClientName: TextView = itemview.findViewById(com.clikzop.sales_standerd.R.id.tvClientName)
        val tvMobile: TextView = itemview.findViewById(com.clikzop.sales_standerd.R.id.tvMobile)
        val tvContactPerson: TextView = itemview.findViewById(com.clikzop.sales_standerd.R.id.tvContactPerson)
        val tvStartTime: TextView = itemview.findViewById(com.clikzop.sales_standerd.R.id.tvStartTime)
        val tvStopTime: TextView = itemview.findViewById(com.clikzop.sales_standerd.R.id.tvStopTime)
        val tvStartLocation: TextView = itemview.findViewById(com.clikzop.sales_standerd.R.id.tvStartLocation)
        val tvStopLocation: TextView = itemview.findViewById(com.clikzop.sales_standerd.R.id.tvStopLocation)
        val tvAddress: TextView = itemview.findViewById(com.clikzop.sales_standerd.R.id.tvAddress)
        val tvRemark: TextView = itemview.findViewById(com.clikzop.sales_standerd.R.id.tvRemark)
        val tvStopMeeting: TextView = itemview.findViewById(com.clikzop.sales_standerd.R.id.tvStopMeeting)

    }
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    mFilteredList = list as MutableList<MettingListBean.Data>
                } else {
                    val filteredList = ArrayList<MettingListBean.Data>()
                    for (serviceBean in list) {
                        if (serviceBean.clientName.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(serviceBean)
                        }
                    }
                    mFilteredList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = mFilteredList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mFilteredList = filterResults.values as ArrayList<MettingListBean.Data>
                android.os.Handler().postDelayed(Runnable { notifyDataSetChanged() }, 200)
            }
        }
    }
}