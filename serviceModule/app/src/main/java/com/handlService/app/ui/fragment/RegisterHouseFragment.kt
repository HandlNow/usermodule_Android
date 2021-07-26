/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.widget.Autocomplete
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentRegisterHouseBinding
import com.handlService.app.model.AddressData
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.extensions.checkString
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.login.TransportationFragment
import com.handlService.app.utils.AddressDecoder
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3MultipartByte
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class RegisterHouseFragment : BaseFragment(), ClickHandler, AddressDecoder.AddressListener, BaseActivity.PermCallback, OnMapReadyCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapLoadedCallback {

    private var binding: FragmentRegisterHouseBinding? = null
    private var googleMap: GoogleMap? = null
    private var currentLatlang: LatLng? = null
    private var addressData: AddressData? = null
    private var currentLocation: Location? = null
    private var addressDecoder: AddressDecoder? = null
    private var getData = true
    private var mLastClickTime = SystemClock.elapsedRealtime()
    private var count: Int = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_register_house, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.register_a_house))
        addressDecoder = AddressDecoder.getInstance(baseActivity!!)
        addressDecoder?.setAddressListener(this)
        initializeMap()
    }

    private fun initializeMap() {
        if (baseActivity!!.checkPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        140,
                        this)) {

            val availableMap = childFragmentManager.findFragmentById(R.id.mapV) as SupportMapFragment?
            availableMap!!.getMapAsync(this)
        }
    }

    override fun onHandleClick(view: View) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (view.id) {
            R.id.doneTV -> {
                if (baseActivity!!.getAddressData() != null) {
                    if (binding!!.addressET.checkString().isNotEmpty()) {
                        hitUpdateApi()
                    } else {
                        showToastOne(baseActivity!!.getString(R.string.enter_location))
                    }
                }
            }
            R.id.addressET -> {
                baseActivity!!.hideSoftKeyboard()
                if (baseActivity!!.checkPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), Const.PLAY_SERVICES_RESOLUTION_REQUEST, this)) {
                    searchLocation(Const.PLAY_SERVICES_RESOLUTION_REQUEST)
                }
            }
        }
    }

    override fun onPlacesAddressFound(addressData: AddressData?) {
        baseActivity?.runOnUiThread {
            this.addressData = addressData
            if (addressData != null) {
                if (addressData.isZoomMap) {
                    getData = false
                    googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(addressData.latitude?.toDouble()
                            ?: 0.0, addressData.longitude?.toDouble() ?: 0.0), 15f))
                }
                binding!!.addressET.setText(addressData.address.toString())
                binding!!.codeET.setText(addressData.zipcode.toString())
                binding!!.cityET.setText(addressData.city.toString())
                baseActivity!!.setAddressData(addressData)

            }
        }

    }

    override fun permGranted(resultCode: Int) {
        if (resultCode == 200) {
            count = 0
            getAndSetCurrentLocation()
        } else if (resultCode == 140) {
            initializeMap()
        } else if (resultCode == Const.PLAY_SERVICES_RESOLUTION_REQUEST) {
            searchLocation(Const.PLAY_SERVICES_RESOLUTION_REQUEST)
        }

    }

    override fun permDenied(resultCode: Int) {

    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(baseActivity!!,R.style.animateDialog)
        builder.setTitle(baseActivity?.getString(R.string.alert))
        builder.setMessage(baseActivity?.getString(R.string.gps_not_enabled))
        builder.setCancelable(false)
        builder.setPositiveButton(baseActivity?.getString(R.string.ok)) { dialog, which ->
            dialog.dismiss()
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 151)
        }
        builder.show()
    }

    private fun getAndSetCurrentLocation() {
        if (baseActivity!!.checkPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 200, this)) {
            val mLocationManager = baseActivity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                currentLocation = addressDecoder!!.getLastKnownLocation(baseActivity!!)
                if (currentLocation != null) {
                    if (googleMap != null) {
                        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(currentLocation!!.latitude, currentLocation!!.longitude), 15f))
                        binding!!.markerIV.visibility = View.VISIBLE
                    } else {
                        binding!!.markerIV.visibility = View.GONE
                    }
                } else {
                    if (count < 5) {
                        count++
                        getAndSetCurrentLocation()
                    } else {
                        getAndSetCurrentLocation()
                    }
                }
            } else {
                if (isAdded && isVisible) {
                    showAlertDialog()

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Const.PLAY_SERVICES_RESOLUTION_REQUEST && data != null) {
            if (resultCode == Activity.RESULT_OK) {
                val place = data.let { Autocomplete.getPlaceFromIntent(it) }
                try {
                    addressDecoder?.saveDataFromPlace(place)
                } catch (e: Exception) {
                    baseActivity?.handleException(e)
                }

            }
        }

    }

    override fun onMapReady(map: GoogleMap?) {

        this.googleMap = map
        if (ActivityCompat.checkSelfPermission(baseActivity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(baseActivity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        googleMap?.isMyLocationEnabled = false
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.uiSettings?.isZoomControlsEnabled = false
        googleMap?.uiSettings?.isTiltGesturesEnabled = false

        googleMap?.setOnCameraIdleListener(this)
        googleMap?.setOnMapLoadedCallback(this)
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
                addressDecoder!!.getAddressDataAsync(currentLatlang!!.latitude, currentLatlang!!.longitude, null)
            }
        } else {
            getData = true
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

    private fun hitUpdateApi() {
        val param = Api3MultipartByte()
        param.put("User[zipcode]", baseActivity!!.getAddressData()?.zipcode!!)
        param.put("User[longitude]", baseActivity!!.getAddressData()?.longitude!!)
        param.put("User[latitude]", baseActivity!!.getAddressData()?.latitude!!)
        param.put("User[city]", baseActivity!!.getAddressData()?.city!!)
        param.put("User[address]", baseActivity!!.getAddressData()?.address!!)
        val call = api!!.apiUpdateProfile(param.getRequestBody())
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_UPDATE_PROFILE)) {
                if (responseCode == Const.STATUS_OK) {
                    try {
                        showToastOne(baseActivity!!.getString(R.string.profile_update_succes))
                        val data = Gson().fromJson<ProfileData>(jsonObjects.getJSONObject("detail").toString(), ProfileData::class.java)
                        baseActivity!!.setProfileData(data)
                        baseActivity!!.updateDrawer()
                        baseActivity!!.replaceFragment(TransportationFragment())
                    } catch (e: Exception) {
                        baseActivity!!.handleException(e)
                    }

                }

            }

        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

}
