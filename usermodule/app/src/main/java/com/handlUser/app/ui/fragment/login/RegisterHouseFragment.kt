/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment.login

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogLocationTurnonBinding
import com.handlUser.app.databinding.FragmentRegisterHouseBinding
import com.handlUser.app.model.AddressData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.activity.LoginSignUpActivity
import com.handlUser.app.ui.activity.MainActivity
import com.handlUser.app.ui.adapter.AdapterAddressHouses
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.extensions.isBlank
import com.handlUser.app.ui.extensions.replaceFragmentWithoutStackWithoutBundle
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.ui.fragment.PaymentMethodListFragment
import com.handlUser.app.utils.AddressDecoder
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class RegisterHouseFragment : BaseFragment(), AddressDecoder.AddressListener,
    BaseActivity.PermCallback, OnMapReadyCallback, GoogleMap.OnCameraIdleListener,
    GoogleMap.OnMapLoadedCallback, ClickHandler, BaseAdapter.OnItemClickListener {

    private var binding: FragmentRegisterHouseBinding? = null
    private var googleMap: GoogleMap? = null
    private var currentLatlang: LatLng? = null
    private var addressData: AddressData? = null
    private var data: AddressData? = null
    private var currentLocation: Location? = null
    private var addressDecoder: AddressDecoder? = null
    private var getData = true
    private var mLastClickTime = SystemClock.elapsedRealtime()
    private var count: Int = 0
    private var userAddressList: ArrayList<AddressData> = ArrayList()
    private var deletePostion: Int? = null
    private var adapter: AdapterAddressHouses? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_register_house, container, false
            )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            data = arguments?.getParcelable("data")
        }
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.register_a_house))
        if (data != null) {
            binding!!.logoIV.visibility = View.GONE
            baseActivity!!.setToolbar(title = getString(R.string.my_saved_homes))
        }
        if (baseActivity is LoginSignUpActivity) {
            binding!!.dotsI.dot3.setImageResource(R.drawable.circle_dark_blue)
            binding!!.logoIV.visibility = View.VISIBLE
        } else {
            binding!!.logoIV.visibility = View.GONE
        }

        addressDecoder = AddressDecoder.getInstance(baseActivity!!)
        addressDecoder?.setAddressListener(this)

        initializeMap()

        when (baseActivity) {
            is MainActivity -> {
                binding!!.doneTV.visibility = View.GONE
            }
        }
        setHasOptionsMenu(true)

    }

    private fun initializeMap() {
        if (baseActivity!!.checkPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                140,
                this
            )
        ) {

            val availableMap =
                childFragmentManager.findFragmentById(R.id.mapV) as SupportMapFragment?
            availableMap!!.getMapAsync(this)
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        this.googleMap = map
        if (ActivityCompat.checkSelfPermission(
                baseActivity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                baseActivity!!,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap?.isMyLocationEnabled = false
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.uiSettings?.isZoomControlsEnabled = false
        googleMap?.uiSettings?.isTiltGesturesEnabled = false

        googleMap?.setOnCameraIdleListener(this)
        googleMap?.setOnMapLoadedCallback(this)
    }


    override fun onHandleClick(view: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (view.id) {
            R.id.addTV -> {
                if (isValidate()) {
                    if (addressData != null) {
                        apiAddAddress()
                    }
                }
            }
            R.id.addressET -> {
                getAddress()
            }
            R.id.doneTV -> {
                if (isValidate()) {
                    if (addressData != null) {
                        apiAddAddress()
                    }
                }

            }
        }
    }

    private fun isValidate(): Boolean {
        when {
            binding!!.addressET.isBlank()
                    || binding!!.codeET.isBlank()
                    || binding!!.cityET.isBlank() -> baseActivity!!.showToastOne(
                baseActivity!!.getString(
                    R.string.enter_complemte_data
                )
            )
            else -> {
                return true
            }
        }
        return false
    }


    private fun apiAddAddress() {
        val param = Api3Params()
        if (binding!!.addressNameET.checkString().isNotEmpty()) {
            param.put("UserAddress[title]", binding!!.addressNameET.checkString())
        } else {
            param.put("UserAddress[title]", "volga 8a")
        }
        param.put("UserAddress[address]", binding!!.addressET.checkString())
        param.put("UserAddress[latitude]", addressData!!.latitude.toString())
        param.put(" UserAddress[longitude]", addressData!!.longitude.toString())
        param.put("UserAddress[city]", binding!!.cityET.checkString())
        param.put("UserAddress[state]", addressData!!.state!!)
        param.put("UserAddress[country]", addressData!!.country!!)
        param.put("UserAddress[zipcode]", binding!!.codeET.checkString())
        if (data != null) {
            val call = api!!.apiUpdateAddress(data!!.id!!, param.getServerHashMap())
            restFullClient!!.sendRequest(call, this)
        } else {
            val call = api!!.apiAddAddress(param.getServerHashMap())
            restFullClient!!.sendRequest(call, this)
        }
    }


    override fun onSyncSuccess(
        responseCode: Int,
        responseMessage: String,
        responseUrl: String,
        response: String?
    ) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_ADD_ADDRESS)) {
                if (responseCode == Const.STATUS_OK) {
                    val detail = jsonObjects.getJSONObject("detail")
                    val data =
                        Gson().fromJson<AddressData>(detail.toString(), AddressData::class.java)
                    userAddressList.add(data)
                    if (userAddressList.size > 0) {
                        baseActivity!!.replaceFragmentWithoutStackWithoutBundle(
                            PaymentMethodListFragment(),
                            R.id.container
                        )
                    } else {
                        showToastOne(getString(R.string.please_add_addresss))
                    }

                }

            } else if (responseUrl.contains(Const.API_UPDATE_ADDRESS)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(getString(R.string.adrress_updated))
                    baseActivity!!.onBackPressed()

                }

            } else if (responseUrl.contains(Const.API_ADDRESS_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    val jsonArray = jsonObjects.getJSONArray("list")
                    userAddressList.clear()
                    for (i in 0 until jsonArray.length()) {
                        val data = Gson().fromJson<AddressData>(
                            jsonArray.toString(),
                            AddressData::class.java
                        )
                        userAddressList.add(data)
                    }

                    if (userAddressList.size > 0) {
                        setAdapter()
                        binding!!.doneTV.background =
                            ContextCompat.getDrawable(baseActivity!!, R.drawable.light_blue_button)
                    } else {
                        binding!!.doneTV.background =
                            ContextCompat.getDrawable(baseActivity!!, R.drawable.grey_button)

                    }

                }
            } else if (responseUrl.contains(Const.API_DELETE_ADDRESS)) {
                if (responseCode == Const.STATUS_OK) {
                    userAddressList.removeAt(deletePostion!!)
                    adapter!!.notifyDataSetChanged()

                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    private fun setAdapter() {
        if (userAddressList.size > 0) {
            adapter = AdapterAddressHouses(baseActivity!!, userAddressList)
            binding!!.addressRV.adapter = adapter
            adapter!!.setOnItemClickListener(this)
        }
    }

    override fun onMapLoaded() {
        count = 0
        getAndSetCurrentLocation()
        if (currentLocation != null) {
            val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        }
    }

    private fun getAndSetCurrentLocation() {
        if (baseActivity!!.checkPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 200, this
            )
        ) {
            val mLocationManager =
                baseActivity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                currentLocation = addressDecoder!!.getLastKnownLocation(baseActivity!!)
                if (currentLocation != null) {
                    if (googleMap != null) {
                        googleMap!!.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    currentLocation!!.latitude,
                                    currentLocation!!.longitude
                                ), 15f
                            )
                        )
                        binding!!.markerIV.visibility = View.VISIBLE
                    } else {
                        binding!!.markerIV.visibility = View.GONE
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
                    baseActivity!!.showAlertDialog()

                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101) {

            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                try {
                    addressDecoder?.saveDataFromPlace(place)
                } catch (e: Exception) {
                    baseActivity?.handleException(e)
                }


            }

        }
    }


    override fun permGranted(resultCode: Int) {

        if (resultCode == 200) {
            count = 0
            getAndSetCurrentLocation()
        } else if (resultCode == 140) {
            initializeMap()
        }
    }

    override fun permDenied(resultCode: Int) {
    }


    override fun onPlacesAddressFound(addressData: AddressData?) {
        baseActivity?.runOnUiThread {
            if (data != null) {
                this.addressData = data
                binding!!.addressNameET.setText(data!!.title)
                binding!!.addressRV.visibility = View.GONE
                binding!!.addTV.text = baseActivity!!.getString(R.string.update_homes)
            } else {
                this.addressData = addressData
            }
            if (addressData != null) {
                if (addressData.isZoomMap) {
                    getData = false
                    googleMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                addressData.latitude?.toDouble()
                                    ?: 0.0, addressData.longitude?.toDouble() ?: 0.0
                            ), 15f
                        )
                    )
                }
                binding!!.addressET.setText(addressData.address.toString())
                binding!!.codeET.setText(addressData.zipcode.toString())
                binding!!.cityET.setText(addressData.city.toString())

            }
        }
    }

    private fun getAddress() {
        if (!Places.isInitialized()) {
            Places.initialize(
                baseActivity!!.applicationContext,
                baseActivity!!.getString(R.string.google_api)
            )
        }
        val fields = listOf(*Place.Field.values())
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        )
            .build(baseActivity!!)
        startActivityForResult(intent, 101)

    }


    override fun onResume() {
        super.onResume()
        baseActivity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        if (googleMap == null) {
            initializeMap()
        } else if (currentLocation == null) {
            count = 0
            getAndSetCurrentLocation()
        }
    }

    override fun onCameraIdle() {
        currentLatlang = googleMap?.cameraPosition?.target
        if (baseActivity!!.isNetworkAvailable && getData) {
            GlobalScope.launch {
                addressDecoder!!.getAddressDataAsync(
                    currentLatlang!!.latitude,
                    currentLatlang!!.longitude,
                    null
                )
            }
        } else {
            getData = true
        }
    }

    fun deleteAddress(addressId: Int, position: Int) {
        deletePostion = position
        val call = api!!.apiDeleteAddress(addressId)
        restFullClient!!.sendRequest(call, this)
    }

    override fun onItemClick(vararg itemData: Any) {
        val type = itemData[0] as Int
        val pos = itemData[1] as Int
        when (type) {
            Const.STATUS_OK -> {
                deleteAddress(userAddressList[pos].id!!, pos)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_logout, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutMB -> {
                baseActivity!!.logOut()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
