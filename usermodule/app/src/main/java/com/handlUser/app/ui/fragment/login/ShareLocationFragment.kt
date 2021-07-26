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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.widget.Autocomplete
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogLocationTurnonBinding
import com.handlUser.app.databinding.FragmentShareLocationBinding
import com.handlUser.app.model.AddressData
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.utils.AddressDecoder
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ShareLocationFragment : BaseFragment(), AddressDecoder.AddressListener,
    BaseActivity.PermCallback, OnMapReadyCallback, GoogleMap.OnCameraIdleListener,
    GoogleMap.OnMapLoadedCallback, ClickHandler {

    private var binding: FragmentShareLocationBinding? = null
    private var googleMap: GoogleMap? = null
    private var currentLatlang: LatLng? = null
    private var currentLocation: Location? = null
    private var addressData: AddressData? = null
    private var addressDecoder: AddressDecoder? = null
    private var getData = true
    private var count: Int = 0
    private var mLastClickTime = SystemClock.elapsedRealtime()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_share_location, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolbar(
            title = getString(R.string.share_location),
            screenBg = ContextCompat.getColor(baseActivity!!, R.color.screen_bg),
            showTitle = true
        )
        binding!!.handleClick = this
        addressDecoder = AddressDecoder.getInstance(baseActivity!!)
        addressDecoder?.setAddressListener(this)

        initializeMap()
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
            R.id.shareTV -> {
                if (binding!!.cityET.checkString().isNotEmpty()) {
                    baseActivity!!.replaceFragment(SelectionFragment())
                } else {
                    showToastOne(baseActivity!!.getString(R.string.please_add_location))
                }
            }
            R.id.cityET -> {
                baseActivity!!.hideSoftKeyboard()
                if (baseActivity!!.checkPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ), Const.PLAY_SERVICES_RESOLUTION_REQUEST, this
                    )
                ) {
                    searchLocation(Const.PLAY_SERVICES_RESOLUTION_REQUEST)
                }
            }
        }
    }

    override fun onMapLoaded() {
        count = 0
        getAndSetCurrentLocation()
        if (currentLocation != null) {
            val latLng = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))

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
                                ), 18f
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

        } else if (requestCode == Const.PLAY_SERVICES_RESOLUTION_REQUEST && data != null) {
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


    override fun onPlacesAddressFound(addressData: AddressData?) {
        baseActivity?.runOnUiThread {
            this.addressData = addressData
            if (addressData != null) {
                if (addressData.isZoomMap) {
                    getData = false
                    googleMap!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                addressData.latitude?.toDouble()
                                    ?: 0.0, addressData.longitude?.toDouble() ?: 0.0
                            ), 18f
                        )
                    )
                }
                binding!!.codeET.setText(addressData.zipcode.toString())
                binding!!.cityET.setText(addressData.city.toString())
                baseActivity!!.setAddressData(addressData)
            }
        }
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


}
