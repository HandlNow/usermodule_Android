/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment.login

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentVerifiedBinding
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.*
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONException
import org.json.JSONObject


class VerifiedFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentVerifiedBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_verified, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        setHasOptionsMenu(true)
        binding!!.handleClick = this
        binding!!.dotsI.dot4.setImageResource(R.drawable.circle_dark_blue)
        baseActivity!!.setToolbar(baseActivity!!.getString(R.string.hi) + baseActivity!!.getProfileData()!!.firstName + "!", ContextCompat.getColor(baseActivity!!, R.color.screen_bg))
        binding!!.pullToRefresh.setOnRefreshListener {
            hitCheckApi()
            binding!!.pullToRefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        hitCheckApi()
    }

    private fun hitCheckApi() {
        val params = Api3Params()
        params.put("DeviceDetail[device_token]", baseActivity!!.getDeviceToken())
        params.put("DeviceDetail[device_type]", Const.ANDROID)
        params.put("DeviceDetail[device_name]", Build.MODEL)
        val call = api!!.apiCheck(params.getServerHashMap())
        restFullClient?.sendRequest(call, this)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.locationTV -> baseActivity!!.replaceFragment(RegisterHouseFragment())

            R.id.myServiceTV -> baseActivity!!.replaceFragment(MyServicesFragment())

            R.id.availTV -> baseActivity!!.replaceFragment(AvailabilitySlotsFragment())

            R.id.subscTV -> baseActivity!!.replaceFragment(SubscriptionFragment())

            R.id.languagesTV -> baseActivity!!.replaceFragment(LanguagesFragment())

            R.id.continueTV -> {
                when {
                    !baseActivity!!.getProfileData()!!.isLocation -> {
                        showToastOne(baseActivity!!.getString(R.string.location_sec))
                    }
                    !baseActivity!!.getProfileData()!!.isService -> {
                        showToastOne(baseActivity!!.getString(R.string.service_sec))
                    }
                    !baseActivity!!.getProfileData()!!.isAvailable -> {
                        showToastOne(baseActivity!!.getString(R.string.avail_sec))
                    }
                    !baseActivity!!.getProfileData()!!.isSubscription -> {
                        showToastOne(baseActivity!!.getString(R.string.subsc_sec))
                    }
                    !baseActivity!!.getProfileData()!!.isLanguage -> {
                        showToastOne(baseActivity!!.getString(R.string.lang_sec))
                    }
                    else -> {
                        baseActivity!!.gotoMainActivity()
                    }
                }
            }
        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        try {
            val respObject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_CHECK)) {
                val data = Gson().fromJson<ProfileData>(respObject.getJSONObject("detail").toString(), ProfileData::class.java)
                baseActivity!!.setProfileData(data)
                restFullClient!!.setLoginStatus(data.accessToken)
                checkData()
            }

        } catch (e: JSONException) {
            handleException(e)
        }
    }

    private fun checkData() {
        val avail = baseActivity!!.getProfileData()!!.isAvailable
        val lang = baseActivity!!.getProfileData()!!.isLanguage
        val subsc = baseActivity!!.getProfileData()!!.isSubscription
        val service = baseActivity!!.getProfileData()!!.isService
        val loc = baseActivity!!.getProfileData()!!.isLocation
        when {
            avail -> {
                binding!!.availTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_tick, 0)
            }
            else -> {
                binding!!.availTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_next_page, 0)
            }
        }
        when {
            loc -> {
                binding!!.locationTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_tick, 0)
            }
            else -> {
                binding!!.locationTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_next_page, 0)
            }
        }
        when {
            service -> {
                binding!!.myServiceTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_tick, 0)
            }
            else -> {
                binding!!.myServiceTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_next_page, 0)
            }
        }
        when {
            subsc -> {
                binding!!.subscTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_tick, 0)
            }
            else -> {
                binding!!.subscTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_next_page, 0)
            }
        }
        when {
            lang -> {
                binding!!.languagesTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_tick, 0)
            }
            else -> {
                binding!!.languagesTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.ic_next_page, 0)
            }
        }

        when {
            avail && loc && service && subsc && lang -> {
                binding!!.continueTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.mid_blue_button)
                binding!!.titleTV.text = baseActivity!!.getString(R.string.you_are_now_ready_to_start_getting_clients)
                binding!!.descTV.text = baseActivity!!.getString(R.string.well_done)
            }
            else -> {
                binding!!.continueTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.lightgrey_button)
                binding!!.titleTV.text = baseActivity!!.getString(R.string.to_start_getting_the_clients_complete_the_sections_below)
                binding!!.descTV.text = baseActivity!!.getString(R.string.you_are_almost_ready_to_start_getting_clients)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_logout, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logoutMB) {
            baseActivity!!.logOut()
        }
        return super.onOptionsItemSelected(item)
    }

}
