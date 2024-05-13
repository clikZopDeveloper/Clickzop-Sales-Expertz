package com.clikzop.sales_standerd.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.clikzop.sales_standerd.Activity.ContactListActivity
import com.clikzop.sales_standerd.Activity.LoginActivity
import com.clikzop.sales_standerd.Activity.PasswordChnageActivity
import com.clikzop.sales_standerd.Activity.ProfileActivity
import com.clikzop.sales_standerd.ApiHelper.ApiController
import com.clikzop.sales_standerd.ApiHelper.ApiResponseListner
import com.clikzop.sales_standerd.Model.BaseResponseBean
import com.clikzop.sales_standerd.R
import com.clikzop.sales_standerd.Utills.GeneralUtilities
import com.clikzop.sales_standerd.Utills.PrefManager
import com.clikzop.sales_standerd.Utills.SalesApp
import com.clikzop.sales_standerd.Utills.Utility

import com.clikzop.sales_standerd.databinding.FragmentSettingBinding
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants

class SettingFragment : Fragment(), ApiResponseListner {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var apiClient: ApiController
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.appbarLayout.ivMenu.setImageDrawable(resources.getDrawable(R.drawable.ic_back_black))
        binding.appbarLayout.ivMenu.setOnClickListener {
            requireActivity().onBackPressed()
        }
        binding.appbarLayout.tvTitle.text = "Setting"

        binding.tvUserName.setText(PrefManager.getString(ApiContants.userName,""))
        binding.tvUserType.setText(PrefManager.getString(ApiContants.UserType,""))
        binding.tvMobNo.setText(PrefManager.getString(ApiContants.mobileNumber,""))
        binding.tvEmail.setText(PrefManager.getString(ApiContants.EmailAddress,""))

        binding.llChangePass.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    PasswordChnageActivity::class.java
                )
            )
        }
        binding.llContactUS.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ContactListActivity::class.java
                )
            )
        }

        binding.llLogout.setOnClickListener {
            apiCallLogout()
        }

        binding.ivProfile.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    ProfileActivity::class.java
                )
            )
        }

        return root

    }

    fun apiCallLogout() {
        SalesApp.isAddAccessToken = true
        apiClient = ApiController(requireContext(), this)
        val params = Utility.getParmMap()
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.logout, params)

    }

    override fun success(tag: String?, jsonElement: JsonElement) {
        try {
            apiClient.progressView.hideLoader()
            if (tag == ApiContants.logout) {
                val baseResponseBean = apiClient.getConvertIntoModel<BaseResponseBean>(
                    jsonElement.toString(),
                    BaseResponseBean::class.java
                )
                Toast.makeText(requireContext(), baseResponseBean.msg, Toast.LENGTH_SHORT).show()
                PrefManager.clear()
                GeneralUtilities.launchActivity(
                    requireActivity() as AppCompatActivity?,
                    LoginActivity::class.java
                )
                requireActivity().finishAffinity()
            }


        } catch (e: Exception) {
            Log.d("error>>", e.localizedMessage)
        }

    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()
        // Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()
        Utility.showSnackBar(requireActivity(), errorMessage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}