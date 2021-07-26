/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentPaymentDetailsBinding
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.login.VerificationFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject


class PaymentDetailsFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentPaymentDetailsBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_payment_details, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.payment))
        setHasOptionsMenu(false)
        setData()
        binding!!.pullToRefresh.setOnRefreshListener {
            hitcheckAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }
    }

    private fun setData() {
        if (baseActivity!!.getProfileData()!!.accountDetail.size > 0) {
            binding!!.ibanValueTV.text = baseActivity!!.getProfileData()!!.accountDetail[0].bankName
            binding!!.accountValueTV.text = baseActivity!!.getProfileData()!!.accountDetail[0].account
            binding!!.swiftValueTV.text = baseActivity!!.getProfileData()!!.accountDetail[0].routingNumber
        }


    }

    private fun hitcheckAPI() {
        val params = Api3Params()
        params.put("DeviceDetail[device_token]", baseActivity!!.getDeviceToken())
        params.put("DeviceDetail[device_type]", Const.ANDROID)
        params.put("DeviceDetail[device_name]", Build.MODEL)
        val call = api!!.apiCheck(params.getServerHashMap())
        restFullClient?.sendRequest(call, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeMB) {
            (baseActivity as MainActivity).openDrawer()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.doneTV -> baseActivity!!.replaceFragment(VerificationFragment())
            R.id.addTV -> baseActivity!!.replaceFragment(AddBankDetailFragment())
        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_CHECK)) {
                val data = Gson().fromJson<ProfileData>(jsonObjects.getJSONObject("detail").toString(), ProfileData::class.java)
                baseActivity!!.setProfileData(data)
                restFullClient!!.setLoginStatus(data.accessToken)
                setData()
            }


        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

}
