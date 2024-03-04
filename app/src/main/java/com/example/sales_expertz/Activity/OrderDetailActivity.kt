package com.example.sales_expertz.Activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sales_expertz.Adapter.OderDetailListAdapter
import com.example.sales_expertz.ApiHelper.ApiController
import com.example.sales_expertz.ApiHelper.ApiResponseListner
import com.example.sales_expertz.Model.CreateOrderBean
import com.example.sales_expertz.Model.MultipleProductBean
import com.example.sales_expertz.Model.OrderDetailBean
import com.example.sales_expertz.R
import com.example.sales_expertz.Utills.*

import com.example.sales_expertz.databinding.ActivityOrderDetailBinding

import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants
import java.util.ArrayList

class OrderDetailActivity : AppCompatActivity(), ApiResponseListner,
    GoogleApiClient.OnConnectionFailedListener,
    ConnectivityListener.ConnectivityReceiverListener {
    private lateinit var binding: ActivityOrderDetailBinding
    private lateinit var apiClient: ApiController
    var myReceiver: ConnectivityListener? = null
    var list: MutableList<MultipleProductBean> = ArrayList()
    var activity: Activity = this
    var customerID=0
    var orderID=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        if (SalesApp.isEnableScreenshort==true){
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        myReceiver = ConnectivityListener()

        binding.igToolbar.tvTitle.text = "Order Detail"
        binding.igToolbar.ivMenu.setImageDrawable(resources.getDrawable(R.drawable.ic_back_black))
        binding.igToolbar.ivMenu.setOnClickListener { finish() }

        intent.getStringExtra("order_id")?.let { apiOrderDetail(it) }
        orderID= intent.getStringExtra("order_id").toString()
        binding.btnReturnOrder.setOnClickListener {
          //  apiCreateReturnOrder(intent.getStringExtra("order_id"))
          //  binding.tvTap.visibility=View.VISIBLE
          //  binding.tvCreateRetrunOrder.visibility=View.VISIBLE
        }
        binding.tvCreateRetrunOrder.setOnClickListener {
            //  apiCreateReturnOrder(intent.getStringExtra("order_id"))
        }
    }

    fun apiOrderDetail(orderID: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(this, this)
        val params = Utility.getParmMap()
        params["order_id"] = orderID
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getOrderDetail, params)

    }

    fun handleOrderDetailList(
        leadProduct: List<OrderDetailBean.Data.OrderDet>
    ) {
        binding.rcCommentList.layoutManager = LinearLayoutManager(this)
        var mAdapter = OderDetailListAdapter(this, leadProduct, object :
            RvCreateOrderClickListner {
            override fun clickPos(status: MultipleProductBean, id: Int) {
               // list.add(status)
              //  apiCreateReturnOrder()
                Log.d("xzczxcxz", Gson().toJson(status))
            }
        })
        binding.rcCommentList.adapter = mAdapter
        // rvMyAcFiled.isNestedScrollingEnabled = false
    }

    fun apiCreateReturnOrder() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        val params = Utility.getParmMap()
        params["customer_id"] =customerID.toString()
        params["products"] = Gson().toJson(list)
        params["order_id"] = orderID
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.CreateReturnOrder, params)
    }

    override fun success(tag: String?, jsonElement: JsonElement?) {
        try {
            apiClient.progressView.hideLoader()
            if (tag == ApiContants.getOrderDetail) {
                val orderDetailBean = apiClient.getConvertIntoModel<OrderDetailBean>(
                    jsonElement.toString(),
                    OrderDetailBean::class.java
                )
                //   Toast.makeText(this, allStatusBean.msg, Toast.LENGTH_SHORT).show()
                if (orderDetailBean.error==false) {
                    binding.tvName.setText(orderDetailBean.data.orderMst.customerName)
                    binding.tvOrderValue.setText(ApiContants.currency+orderDetailBean.data.orderMst.orderValue)
                    binding.tvOrderDate.setText(orderDetailBean.data.orderMst.orderDate)
                    binding.tvStaus.setText(orderDetailBean.data.orderMst.status)
                     customerID=orderDetailBean.data.orderMst.customerId
                    handleOrderDetailList(orderDetailBean.data.orderDet)

                }
                if (tag == ApiContants.CreateReturnOrder) {
                    val createOrderBean = apiClient.getConvertIntoModel<CreateOrderBean>(
                        jsonElement.toString(),
                        CreateOrderBean::class.java
                    )

                    Toast.makeText(this, createOrderBean.msg, Toast.LENGTH_SHORT).show()
                    finish()
                }

            }
        }catch (e:Exception){
            Log.d("error>>",e.localizedMessage)
        }
    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()
        Utility.showSnackBar(this, errorMessage)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        GeneralUtilities.unregisterBroadCastReceiver(this, myReceiver)
    }

    override fun onResume() {
        GeneralUtilities.registerBroadCastReceiver(this, myReceiver)
        SalesApp.setConnectivityListener(this)
        super.onResume()
    }

    override fun onNetworkConnectionChange(isconnected: Boolean) {
        ApiContants.isconnectedtonetwork = isconnected
        GeneralUtilities.internetConnectivityAction(this, isconnected)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onDestroy() {
        super.onDestroy()
        // Start the LocationService when the app is closed
    //    startService(Intent(this, LocationService::class.java))
    }
}
