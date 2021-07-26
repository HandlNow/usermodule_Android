/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogLocationTurnonBinding
import com.handlUser.app.databinding.FragmentHomeBinding
import com.handlUser.app.model.AddressData
import com.handlUser.app.model.CategoryData
import com.handlUser.app.ui.activity.MainActivity
import com.handlUser.app.ui.adapter.AdapterServiceCategory
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.extensions.afterTextChanged
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.login.NotificationFragment
import com.handlUser.app.utils.AddressDecoder
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import org.json.JSONObject


class HomeFragment : BaseFragment(), ClickHandler, AddressDecoder.AddressListener, BaseAdapter.OnPageEndListener {

    private var binding: FragmentHomeBinding? = null
    private var currentLocation: Location? = null
    private var addressData: AddressData? = null
    private var addressDecoder: AddressDecoder? = null
    private var userAddressList: ArrayList<AddressData> = arrayListOf()
    private var list: ArrayList<CategoryData> = arrayListOf()
    private var isSingle = false
    private var pageCount = 0
    private var adapter: AdapterServiceCategory? = null

    private var searchedText: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_home, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity!!.setToolbar("", ContextCompat.getColor(baseActivity!!, R.color.White), false)
        initUI()
    }

    private fun initUI() {
        resetList()
        userAddressList.clear()
        binding!!.optionI.handleClick = this

        binding!!.searchET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT && binding!!.searchET.text.toString().trim().isNotEmpty()) {
                baseActivity!!.hideSoftKeyboard()
            }
            false
        }
        addressDecoder = AddressDecoder.getInstance(baseActivity!!)
        addressDecoder?.setAddressListener(this)

        getAndSetCurrentLocation()
        hitAddressAPI()

        binding!!.searchET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT && binding!!.searchET.text.toString().trim().isNotEmpty()) {
                searchedText = binding!!.searchET.checkString()
                resetList()
                hitCategoryListAPI()
            }
            false
        }

        binding!!.crossIV.setOnClickListener {
            searchedText = ""
            binding!!.searchET.setText(searchedText)
            resetList()
            hitCategoryListAPI()
        }

        binding!!.searchET.afterTextChanged {
            if (it.isNotEmpty()) {
                binding!!.crossIV.visibility = View.VISIBLE
            } else {
                binding!!.crossIV.visibility = View.GONE
            }
        }

        binding!!.optionI.countTV.text = baseActivity!!.getProfileData()?.notificationCount.toString()

        binding!!.pullToRefresh.setOnRefreshListener {
            searchedText = ""
            binding!!.searchET.setText(searchedText)
            resetList()
            hitCategoryListAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }

    }

    private fun resetList() {
        list.clear()
        pageCount = 0
        isSingle = false
        if (adapter!=null) {
            adapter = null
        }
    }

    private fun hitAddressAPI() {
        val call = api!!.apiAddressList()
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_ADD_ADDRESS)) {
                if (responseCode == Const.STATUS_OK) {
                    val jsonArray = jsonobject.getJSONArray("list")
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AddressData>(object1.toString(), AddressData::class.java)
                        userAddressList.add(data)
                    }
                }

            } else if (responseUrl.contains(Const.API_PROVIDER_CATEGORY_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    pageCount++
                    val totalId = jsonobject.getJSONObject(Const._META).getInt(Const.PAGE_COUNT)
                    isSingle = pageCount >= totalId
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<CategoryData>(object1.toString(), CategoryData::class.java)
                        list.add(data)
                    }
                    if (list.size > 0) {
                        binding!!.noDataTV.visibility = View.GONE
                        binding!!.cleaningRV.visibility = View.VISIBLE
                        setAdapter()
                    } else {
                        binding!!.noDataTV.visibility = View.VISIBLE
                        binding!!.cleaningRV.visibility = View.GONE
                    }

                } else {
                    isSingle = false
                }
            }

        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = AdapterServiceCategory(baseActivity!!, list)
            binding!!.cleaningRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun hitCategoryListAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetCategoryList(pageCount, searchedText)
            restFullClient!!.sendRequest(call, this)
        }
    }

    override fun onPageEnd(vararg itemData: Any) {
        hitCategoryListAPI()
    }

    private var count: Int = 0

    private fun getAndSetCurrentLocation() {
        if (baseActivity!!.checkPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 200, this)) {
            val mLocationManager = baseActivity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                currentLocation = baseActivity!!.googleApiHandle!!.getLastKnownLocation(baseActivity!!)
                if (currentLocation != null) {
                    try {
                        addressDecoder?.saveDataFromPlace(LatLng(currentLocation!!.latitude, currentLocation!!.longitude))
                    } catch (e: Exception) {
                        baseActivity?.handleException(e)
                    }
                } else {
                    if (count < 5) {
                        count++
                        getAndSetCurrentLocation()
                    } else {
                        showToastOne(getString(R.string.not_able_to_get_latlong))
                    }
                }
            } else {
                if (isAdded && isVisible) {
                    showAlertDialog()
                }
            }
        }

    }

    private var dBinding: DialogLocationTurnonBinding? = null

    private fun showAlertDialog() {
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        dBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dialog_location_turnon, null, false)
        mBuilder.setView(dBinding!!.root)
        mBuilder.setCancelable(false)
        val alertDialogd = mBuilder.create()
        alertDialogd.show()
        dBinding!!.noTV.setOnClickListener {
            alertDialogd.dismiss()
        }
        dBinding!!.yesTV.setOnClickListener {
            alertDialogd.dismiss()
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 151)
        }
    }

    override fun onHandleClick(view: View) {

        when (view.id) {
            R.id.menuIV -> {
                (baseActivity as MainActivity).openDrawer()
            }
            R.id.notiIV -> {
                baseActivity!!.replaceFragment(NotificationFragment())
            }
            R.id.searchTV, R.id.downIV -> {
                val popupMenu = PopupMenu(baseActivity!!, binding!!.optionI.searchTV)

                for (i in 0 until userAddressList.size) {
                    popupMenu.menu.add(Const.ONE, i, i, userAddressList[i].title)
                }
                popupMenu.menu.add(Const.ONE, userAddressList.size, userAddressList.size, baseActivity!!.getString(R.string.add_new_building))
                val item = popupMenu.menu.getItem(userAddressList.size)
                val s = SpannableString(baseActivity!!.getString(R.string.add_new_building))
                s.setSpan(ForegroundColorSpan(ContextCompat.getColor(baseActivity!!, R.color.light_blue)), 0, s.length, 0)
                item.title = s

                popupMenu.setOnMenuItemClickListener { item ->
                    if (item.itemId == userAddressList.size) {
                        baseActivity!!.replaceFragment(MySavedHomesFragment())
                    } else {
                        binding!!.optionI.searchTV.text = item.title
                        baseActivity!!.store!!.saveString(Const.HOME_LAT, userAddressList[item.itemId].latitude
                                ?: "")
                        baseActivity!!.store!!.saveString(Const.HOME_LONG, userAddressList[item.itemId].longitude
                                ?: "")
                        baseActivity!!.store!!.saveString(Const.HOME_ADD, userAddressList[item.itemId].address
                                ?: "")
                        resetList()
                        hitCategoryListAPI()
                    }
                    false
                }
                popupMenu.show()
            }
        }
    }

    override fun onPlacesAddressFound(addressData: AddressData?) {
        baseActivity?.runOnUiThread {
            this.addressData = addressData
            if (addressData != null) {
                binding!!.optionI.searchTV.text = addressData.thorough
                baseActivity!!.store!!.saveString(Const.HOME_LAT, addressData.latitude
                        ?: "")
                baseActivity!!.store!!.saveString(Const.HOME_ADD, addressData.address
                        ?: "")
                baseActivity!!.store!!.saveString(Const.HOME_LONG, addressData.longitude
                        ?: "")
                resetList()
                hitCategoryListAPI()
            }
        }

    }

}
