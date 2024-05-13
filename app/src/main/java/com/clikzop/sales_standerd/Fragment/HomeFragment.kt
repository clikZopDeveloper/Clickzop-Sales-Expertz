package com.clikzop.sales_standerd.Fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.clikzop.kktext.Model.StateBean
import com.clikzop.sales_standerd.Activity.*
import com.clikzop.sales_standerd.ApiHelper.ApiController
import com.clikzop.sales_standerd.ApiHelper.ApiResponseListner
import com.clikzop.sales_standerd.Model.*
import com.clikzop.sales_standerd.R
import com.clikzop.sales_standerd.Utills.*
import com.clikzop.sales_standerd.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants
import java.util.*

class HomeFragment : Fragment(), ApiResponseListner, View.OnClickListener {
    private var areaReport = ""
    private lateinit var apiClient: ApiController
    private var _binding: FragmentHomeBinding? = null
    private var currentLoc: String? = null
    private val permissionId = 2
    var list: List<Address>? = null
    private val binding get() = _binding!!
    val bundle = Bundle()
    var officebreak=false
    var daysStatus=false
    var officebreakID=0
    lateinit var shopActivity: DashboardActivity
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onAttach(context: Context) {
        super.onAttach(context)
        shopActivity = activity as DashboardActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLocation()

        binding.refreshLayout.setOnRefreshListener {
            //  apiGetDashboard()
            apiAllGet()
            binding.refreshLayout.isRefreshing = false
        }

        apiAllGet()

        setupFabButtons()

        binding.tvUserName.setText("Hello, "+PrefManager.getString(ApiContants.userName,""))

        /* binding.fbAddArchitect.setOnClickListener {
             //  callPGURL("https://atulautomotive.online/architect-signup")

         }*/
        handleDayStatus()

        binding.fbAddArchitect.setOnClickListener {
            startActivity(
                Intent(
                    requireActivity(),
                    CreateExpensesActivity::class.java
                ).putExtra("way", "CreateExpenses")
            )
        }
        binding.llPending.setOnClickListener {
            bundle.putString("KeyStatus","pending")
            findNavController().navigate(R.id.action_navigation_home_to_navigation_order)
            //     (shopActivity).replaceFrag(R.id.action_ShopCategoryFragment_to_ShopSubCategoryFragment, "", data[pos].categoryID, data[pos].categoryName)

        }
        binding.llDeliverd.setOnClickListener {
            bundle.putString("KeyStatus","delivered")
            findNavController().navigate(R.id.action_navigation_home_to_navigation_order,bundle)

        }
        binding.llApproved.setOnClickListener {
            bundle.putString("KeyStatus","approved")
            findNavController().navigate(R.id.action_navigation_home_to_navigation_order,bundle)

        }
        binding.llRejected.setOnClickListener {
            bundle.putString("KeyStatus","rejected")
            findNavController().navigate(R.id.action_navigation_home_to_navigation_order,bundle)

        }
        binding.llDispatched.setOnClickListener {
            bundle.putString("KeyStatus","dispatched")
            findNavController().navigate(R.id.action_navigation_home_to_navigation_order,bundle)

        }

        return root
    }

    fun handleDayStatus() {

        if (PrefManager.getString(ApiContants.officeBreakStatus, "").equals("start")) {
            binding.switchOfficeBreak.isChecked = true
            binding.officeStartStatus.text = "Office Break Start"
        } else {
            binding.switchOfficeBreak.isChecked = false
            binding.officeStartStatus.text = "Office Break End"
        }

        if (PrefManager.getString(ApiContants.updateOfficeBreak, "").equals("start")) {
            binding.switchUpdateOffice.isChecked = true
        } else {
            binding.switchUpdateOffice.isChecked = false
        }

        binding.switchUpdateOffice.setOnCheckedChangeListener({ _, isChecked ->
            if (isChecked) {
                getLocation()
                dialog("Start Break")
                PrefManager.putString(ApiContants.updateOfficeBreak, "start")
            } else {
                getLocation()
                dialog("End Break")
                PrefManager.putString(ApiContants.updateOfficeBreak, "end")
            }
        })
    }

    fun handleDaysStatus(){
        binding.switchDayStart.setOnClickListener {
            getLocation()
            dialogRemark()
        }

    }
    fun handleOfficeBreak(){
        binding.switchOfficeBreak.setOnCheckedChangeListener({ _, isChecked ->
            if (isChecked) {
                binding.officeStartStatus.text = "Office Break Start"
                getLocation()
                dialog("Start Break")
                PrefManager.putString(ApiContants.officeBreakStatus, "start")
            } else {
                binding.officeStartStatus.text = "Office Break End"
                getLocation()
                dialog("End Break")
                PrefManager.putString(ApiContants.officeBreakStatus, "end")
            }
        })
    }

    fun apiDayStatus(dayStatus: String, officeStatus: String, rmarks: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(requireContext(), this)
        val params = Utility.getParmMap()
        params["last_location"] = "${list?.get(0)?.latitude},${list?.get(0)?.longitude}"
        params["office_status"] = officeStatus
        params["remarks"] = rmarks
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(dayStatus, params)

    }

    fun apiOfficeBreakStatus(breakStatus: String) {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(requireContext(), this)
        val params = Utility.getParmMap()
        if (officebreak==true){
            params["id"] = officebreakID.toString()
        }
        params["break_status"] = breakStatus
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getOfficeBreak, params)

    }

    fun apiAllGet() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(activity, this)
        val params = Utility.getParmMap()
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getDashboard, params)
    }

    override fun success(tag: String?, jsonElement: JsonElement) {
        try {
            apiClient.progressView.hideLoader()

            if (tag == ApiContants.logout) {
                val baseResponseBean = apiClient.getConvertIntoModel<BaseResponseBean>(
                    jsonElement.toString(),
                    BaseResponseBean::class.java
                )
                Toast.makeText(activity, baseResponseBean.msg, Toast.LENGTH_SHORT).show()
                PrefManager.clear()
                GeneralUtilities.launchActivity(
                    requireContext() as AppCompatActivity?,
                    LoginActivity::class.java
                )
                requireActivity().finishAffinity()
            }

            if (tag == ApiContants.getState) {
                val stateBean = apiClient.getConvertIntoModel<StateBean>(
                    jsonElement.toString(),
                    StateBean::class.java
                )
                if (stateBean.error == false) {
                    SalesApp.stateList.clear()
                    SalesApp.stateList.addAll(stateBean.data)
                }
            }

            if (tag == ApiContants.getDashboard) {
                val salesmanDashboardBean = apiClient.getConvertIntoModel<DashboardBean>(
                    jsonElement.toString(),
                    DashboardBean::class.java
                )

                if (salesmanDashboardBean.error == false) {
                    if (salesmanDashboardBean.data.areaReport != null) {
                        areaReport = salesmanDashboardBean.data.areaReport
                    }
                    binding.apply {
                        tvPending.text = salesmanDashboardBean.data.pendingOrder
                        tvDeliverd.text = salesmanDashboardBean.data.deliveredOrder
                        tvApproved.text = salesmanDashboardBean.data.approvedOrder
                        tvRejected.text = salesmanDashboardBean.data.rejectedOrder
                        tvDispatched.text = salesmanDashboardBean.data.dispatchedOrder
                        tvThisMonthExpenses.text =
                            ApiContants.currency + salesmanDashboardBean.data.thisMonthExpense
                        tvLastMonthExpenses.text =
                            ApiContants.currency + salesmanDashboardBean.data.lastMonthExpense
                    }

                    if (salesmanDashboardBean.data.officeBreakStatus==true){
                         officebreak=salesmanDashboardBean.data.officeBreakStatus
                         officebreakID=salesmanDashboardBean.data.officeBreakData.id
                        handleOfficeBreak()
                        binding.switchOfficeBreak.setChecked(true)
                    }else{
                        binding.switchOfficeBreak.setChecked(false)
                        handleOfficeBreak()
                    }

                    if (salesmanDashboardBean.data.dayStatus==true){
                        daysStatus=salesmanDashboardBean.data.dayStatus
                        handleDaysStatus()
                        binding.switchDayStart.setChecked(true)
                        binding.dayStartStatus.text = "Day Start"
                    }else{
                        binding.switchDayStart.setChecked(false)
                        handleDaysStatus()
                        binding.dayStartStatus.text = "Day End"
                    }

                    binding.llThisMonthExpens.setOnClickListener {
                        startActivity(
                            Intent(
                                context,
                                AllExpensesActivity::class.java
                            ).putExtra("thisMonth", salesmanDashboardBean.data.thisMonthExpense)
                                .putExtra("lastMonth", salesmanDashboardBean.data.lastMonthExpense)
                        )
                    }

                    binding.llLastMonthExpens.setOnClickListener {
                        startActivity(
                            Intent(
                                context,
                                AllExpensesActivity::class.java
                            ).putExtra("thisMonth", salesmanDashboardBean.data.thisMonthExpense)
                                .putExtra("lastMonth", salesmanDashboardBean.data.lastMonthExpense)
                        )
                    }
                    //   handleRcDashboard(salesmanDashboardBean.data)

                } else {
                    Toast.makeText(activity, salesmanDashboardBean.msg, Toast.LENGTH_SHORT).show()
                }
            }

            if (tag == ApiContants.startDay) {
                val dayStatusBean = apiClient.getConvertIntoModel<StartDayBean>(
                    jsonElement.toString(),
                    StartDayBean::class.java
                )
                if (dayStatusBean.error == false) {
                    binding.dayStartStatus.text = "Day Start"
                    Utility.showSnackBar(requireActivity(), dayStatusBean.msg)
                }
            }

            if (tag == ApiContants.endDay) {
                val dayStatusBean = apiClient.getConvertIntoModel<EndDayBean>(
                    jsonElement.toString(),
                    EndDayBean::class.java
                )
                if (dayStatusBean.error == false) {
                    binding.dayStartStatus.text = "Day End"
                    Utility.showSnackBar(requireActivity(), dayStatusBean.msg)
                }
            }

            if (tag == ApiContants.getOfficeBreak) {
                val officeBreakStatusBean = apiClient.getConvertIntoModel<OfficeBreakStatusBean>(
                    jsonElement.toString(),
                    OfficeBreakStatusBean::class.java
                )
                if (officeBreakStatusBean.error == false) {
                    Utility.showSnackBar(requireActivity(), officeBreakStatusBean.msg)
                }
            }

        } catch (e: Exception) {
            Log.d("error>>", e.localizedMessage)
        }
    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()
        Utility.showSnackBar(requireActivity(), errorMessage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun dialog(wayType: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Are you sure you want to "+wayType+"?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                // Delete selected note from database
                dialog.dismiss()
                if (wayType.equals("Start Break")) {
                    apiOfficeBreakStatus("Start")
                } else {
                    apiOfficeBreakStatus("Stop")
                }

            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()

                /*if (binding.switchOfficeBreak.isChecked==true){
                    binding.switchOfficeBreak.isChecked=false
                }else{
                    binding.switchOfficeBreak.isChecked=true
                }*/
            }
        val alert = builder.create()
        alert.show()

    }

    ///////////////////////Location//////////////////////////////

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission","SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        Log.d("zxxzv", "Lat" + Gson().toJson(list?.get(0)?.latitude))
                        Log.d("zxxzv", "Long" + Gson().toJson(list?.get(0)?.longitude))
                        Log.d("zxxzv", Gson().toJson(list?.get(0)?.countryName))
                        Log.d("zxxzv", Gson().toJson(list?.get(0)?.locality))
                        Log.d("zxxzv", Gson().toJson(list?.get(0)?.getAddressLine(0)))

                        currentLoc = list?.get(0)?.getAddressLine(0)
                        /*    mainBinding.apply {
                                tvLatitude.text = "Latitude\n${list[0].latitude}"
                                tvLongitude.text = "Longitude\n${list[0].longitude}"
                                tvCountryName.text = "Country Name\n${list[0].countryName}"
                                tvLocality.text = "Locality\n${list[0].locality}"
                                tvAddress.text = "Address\n${list[0].getAddressLine(0)}"
                            }*/
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please turn on location", Toast.LENGTH_LONG)
                    .show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        } else {
            //  checkPermissions()
        }
    }

    private fun setupFabButtons() {
        binding.fabMenuActions.shrink()
        binding.fabMenuActions.setOnClickListener(this)
        binding.fabCreateExpnse.setOnClickListener(this)
        binding.fabCreateOrder.setOnClickListener(this)
        binding.fabCreateROder.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.fab_menu_actions -> {
                expandOrCollapseFAB()
            }
            R.id.fab_create_expnse -> {
                Log.d("asasd", "Hello create_expnse")
                startActivity(
                    Intent(
                        requireActivity(),
                        CreateExpensesActivity::class.java
                    ).putExtra("way", "CreateExpenses")
                )
            }
            R.id.fab_create_order -> {
                Log.d("asasd", "Hello create_order")
                startActivity(
                    Intent(
                        requireActivity(),
                        CreateOrderActivity::class.java
                    ).putExtra("way", "CreateOrder")
                )
            }
            R.id.fab_create_R_oder -> {

                startActivity(
                    Intent(
                        requireActivity(),
                        CreateReturnOrderActivity::class.java
                    ).putExtra("way", "CreateReturnOrder")
                )
                //Log.d("asasd","Hello create_return_order")
            }
        }
    }

    private fun expandOrCollapseFAB() {
        if (binding.fabMenuActions.isExtended) {
            binding.fabMenuActions.shrink()
            binding.fabCreateExpnse.hide()
            binding.fabCreateOrder.hide()
            binding.fabCreateROder.hide()
            binding.tvCreateOrder.visibility = View.GONE
            binding.tvCreateExpnse.visibility = View.GONE
            binding.tvCreateROder.visibility = View.GONE
        } else {
            binding.fabMenuActions.extend()
            binding.fabCreateExpnse.show()
            binding.fabCreateROder.hide()
            binding.fabCreateOrder.show()
            binding.tvCreateExpnse.visibility = View.VISIBLE
            binding.tvCreateOrder.visibility = View.VISIBLE
            binding.tvCreateROder.visibility = View.GONE
        }
    }

    fun dialogRemark() {
        val builder = AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .create()
        val dialog = layoutInflater.inflate(R.layout.dialog_days_reamrk, null)
        builder.setView(dialog)

        builder.setCanceledOnTouchOutside(false)
        builder.show()
        /*    val dialog: Dialog = GeneralUtilities.openBootmSheetDailog(
                R.layout.dialog_update_client, R.style.AppBottomSheetDialogTheme,
                this
            )*/
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val editReamrk =
            dialog.findViewById<TextInputEditText>(R.id.editReamrk) as TextInputEditText
        val tvInOffice = dialog.findViewById<TextInputEditText>(R.id.tvInOffice) as MaterialButton
        val tvOutOffice = dialog.findViewById<TextInputEditText>(R.id.tvOutOffice) as MaterialButton
        var  dayStausType = "in-office"
        tvInOffice.setOnClickListener {
            tvInOffice.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.colorPrimary
                )
            );
            tvOutOffice.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.v_blue_color
                )
            );
            dayStausType = "in-office"
        }
        tvOutOffice.setOnClickListener {
            tvInOffice.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.v_blue_color
                )
            );
            tvOutOffice.setBackgroundTintList(
                ContextCompat.getColorStateList(
                    requireContext(),
                    R.color.colorPrimary
                )
            );
            dayStausType = "out-office"
        }
        val btnSubmit = dialog.findViewById<TextView>(R.id.btnSubmit) as TextView

        ivClose.setOnClickListener { builder.dismiss()
            if (binding.switchDayStart.isChecked==true){
                binding.switchDayStart.isChecked=false
            }else{
                binding.switchDayStart.isChecked=true
            }}
        btnSubmit.setOnClickListener {
            if (editReamrk.text.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Enter Remark", Toast.LENGTH_SHORT).show()
            } else {
                builder.dismiss()
                if (daysStatus==true){
                    apiDayStatus(ApiContants.endDay, dayStausType,editReamrk.text.toString())

                }else{
                    apiDayStatus(ApiContants.startDay, dayStausType,editReamrk.text.toString())
                }
                /*if (dayStausType.equals("in_office")) {

                } else {

                }*/
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().startService(Intent(requireActivity(), LocationService::class.java))
    }

}