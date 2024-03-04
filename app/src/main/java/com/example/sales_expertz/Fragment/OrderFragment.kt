package com.example.sales_expertz.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sales_expertz.Activity.CreateOrderActivity
import com.example.sales_expertz.Activity.OrderDetailActivity
import com.example.sales_expertz.Adapter.OrderListAdapter
import com.example.sales_expertz.ApiHelper.ApiController
import com.example.sales_expertz.ApiHelper.ApiResponseListner
import com.example.sales_expertz.Model.OrderListBean
import com.example.sales_expertz.R
import com.example.sales_expertz.Utills.RvStatusComplClickListner
import com.example.sales_expertz.Utills.SalesApp
import com.example.sales_expertz.Utills.Utility
import com.example.sales_expertz.databinding.FragmentOrderBinding
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants

class OrderFragment : Fragment() , ApiResponseListner {

    private var _binding: FragmentOrderBinding? = null
    private lateinit var apiClient: ApiController

    private val binding get() = _binding!!
var statusType="pending"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.appbarLayout.ivMenu.setImageDrawable(resources.getDrawable(R.drawable.ic_back_black))
        binding.appbarLayout.ivMenu.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.appbarLayout.tvTitle.text = "Orders"
        binding.fbAddMeting.setOnClickListener {
            startActivity(Intent(requireContext(), CreateOrderActivity::class.java).putExtra("way", "CreateOrder"))
        }
        apiClient = ApiController(requireContext(), this)
        typeMode()
        apiGetOrder(statusType)

        return root
    }

    fun typeMode() {
        binding.RadioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                if (checkedId == R.id.rbPending) {
                    binding.rbPending.setTextColor(getResources().getColor(R.color.black));
                    binding.rbApproved.setTextColor(getResources().getColor(R.color.white));
                    binding.rbDispatched.setTextColor(getResources().getColor(R.color.white));
                    binding.rbDelivered.setTextColor(getResources().getColor(R.color.white));
                    binding.rbRejected.setTextColor(getResources().getColor(R.color.white));
                    statusType = "pending"
                    apiGetOrder(statusType)
                } else if (checkedId == R.id.rbApproved) {
                    binding.rbPending.setTextColor(getResources().getColor(R.color.white));
                    binding.rbApproved.setTextColor(getResources().getColor(R.color.black));
                    binding.rbDispatched.setTextColor(getResources().getColor(R.color.white));
                    binding.rbDelivered.setTextColor(getResources().getColor(R.color.white));
                    binding.rbRejected.setTextColor(getResources().getColor(R.color.white));
                    statusType = "approved"
                    apiGetOrder(statusType)
                } else if (checkedId == R.id.rbDispatched) {
                    binding.rbPending.setTextColor(getResources().getColor(R.color.white));
                    binding.rbApproved.setTextColor(getResources().getColor(R.color.white));
                    binding.rbDispatched.setTextColor(getResources().getColor(R.color.black));
                    binding.rbDelivered.setTextColor(getResources().getColor(R.color.white));
                    binding.rbRejected.setTextColor(getResources().getColor(R.color.white));
                    statusType = "dispatched"
                    apiGetOrder(statusType)
                }else if (checkedId == R.id.rbDelivered) {
                    binding.rbPending.setTextColor(getResources().getColor(R.color.white));
                    binding.rbApproved.setTextColor(getResources().getColor(R.color.white));
                    binding.rbDispatched.setTextColor(getResources().getColor(R.color.white));
                    binding.rbDelivered.setTextColor(getResources().getColor(R.color.black));
                    binding.rbRejected.setTextColor(getResources().getColor(R.color.white));
                    statusType = "delivered"
                    apiGetOrder(statusType)
                }else if (checkedId == R.id.rbRejected) {
                    binding.rbPending.setTextColor(getResources().getColor(R.color.white));
                    binding.rbApproved.setTextColor(getResources().getColor(R.color.white));
                    binding.rbDispatched.setTextColor(getResources().getColor(R.color.white));
                    binding.rbDelivered.setTextColor(getResources().getColor(R.color.white));
                    binding.rbRejected.setTextColor(getResources().getColor(R.color.black));
                    statusType = "rejected"
                    apiGetOrder(statusType)
                }
            }
        })
    }

    fun apiGetOrder(statusType: String) {
        SalesApp.isAddAccessToken = true

        val params = Utility.getParmMap()
          apiClient.progressView.showLoader()
        params["status"]=statusType
        apiClient.getApiPostCall(ApiContants.GetOrders,params)

    }

    override fun success(tag: String?, jsonElement: JsonElement?) {
        try {
            apiClient.progressView.hideLoader()
            if (tag == ApiContants.GetOrders) {
                val orderBean = apiClient.getConvertIntoModel<OrderListBean>(
                    jsonElement.toString(),
                    OrderListBean::class.java
                )
                if (orderBean.error == false) {
                    handleOrderList(orderBean.data)
                }
            }
        }catch (e:Exception){

        }
    }

    override fun failure(tag: String?, errorMessage: String?) {
        apiClient.progressView.hideLoader()
        Utility.showSnackBar(requireActivity(), errorMessage!!)
    }

    fun handleOrderList(data: List<OrderListBean.Data>) {
        binding.rcTeamContactList.layoutManager = LinearLayoutManager(requireContext())
        var mAdapter = OrderListAdapter(requireActivity(), data, object :
            RvStatusComplClickListner {
            override fun clickPos(status: String,workstatus: String,amt: String, id: Int) {
                     startActivity(
                         Intent(
                             requireContext(),
                             OrderDetailActivity::class.java
                         ).putExtra("order_id", id.toString())
                     )
            }
        })
        binding.rcTeamContactList.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}