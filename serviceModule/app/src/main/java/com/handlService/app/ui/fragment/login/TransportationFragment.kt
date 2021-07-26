/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.text.Html
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.widget.Autocomplete
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentTranportationBinding
import com.handlService.app.model.AddressData
import com.handlService.app.model.ProfileData
import com.handlService.app.model.UserAddressListModel
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.activity.LoginSignUpActivity
import com.handlService.app.ui.adapter.AdapterTransportationLocation
import com.handlService.app.ui.adapter.BaseAdapter
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.AddressDecoder
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3MultipartByte
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.cos
import kotlin.math.ln


class TransportationFragment : BaseFragment(), ClickHandler, BaseAdapter.OnPageEndListener, BaseAdapter.OnItemClickListener, BaseActivity.PermCallback, GoogleMap.OnCameraIdleListener, GoogleMap.OnMapLoadedCallback, OnMapReadyCallback, AddressDecoder.AddressListener {

    private var circle: Circle? = null
    private var binding: FragmentTranportationBinding? = null
    private var list: ArrayList<UserAddressListModel> = arrayListOf()
    private var isSingle = false
    private var isFirst = false
    private var pageCount = 0
    private var adapter: AdapterTransportationLocation? = null

    private var googleMap: GoogleMap? = null
    private var currentLatlang: LatLng? = null
    private var addressData: AddressData? = null
    private var currentLocation: Location? = null
    private var addressDecoder: AddressDecoder? = null
    private var getData = true
    private var count: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_tranportation, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun hitUpdateApi(typeOn: Int) {
        val param = Api3MultipartByte()
        param.put("User[is_transpotation]", typeOn.toString())
        val call = api!!.apiUpdateProfile(param.getRequestBody())
        restFullClient!!.sendRequest(call, this)
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        isFirst = true
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.transportation), screenBg = ContextCompat.getColor(baseActivity!!, R.color.screen_bg))
        binding!!.view1.text = Html.fromHtml(baseActivity!!.getString(R.string.own_mean_to_clienthome))
        resetList()
        binding!!.seekBarSB.setIndicatorTextFormat("\${PROGRESS}km")
        //map
        addressDecoder = AddressDecoder.getInstance(baseActivity!!)
        addressDecoder?.setAddressListener(this)

        initializeMap()

        if (baseActivity!!.getProfileData() != null) {
            binding!!.checkCB.isChecked = baseActivity!!.getProfileData()!!.isTranspotation != Const.TYPE_OFF
        }

//        if (binding!!.checkCB.isChecked) {
//            binding!!.tranSCL.visibility = View.VISIBLE
            hitLocationListAPI()
//        } else {
//            binding!!.tranSCL.visibility = View.GONE
//            binding!!.doneTV.visibility = View.GONE
//        }

        binding!!.checkCB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding!!.tranSCL.visibility = View.VISIBLE
                binding!!.doneTV.visibility = View.VISIBLE
                hitUpdateApi(Const.TYPE_ON)
                hitLocationListAPI()
            } else {
                binding!!.tranSCL.visibility = View.GONE
                binding!!.doneTV.visibility = View.GONE
                hitUpdateApi(Const.TYPE_OFF)
            }

        }

        binding!!.kmTV.text = binding!!.seekBarSB.progress.toString() + baseActivity!!.getString(R.string.km)

        binding!!.seekBarSB.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                setCircle(seekParams?.progressFloat!!)
                binding!!.kmTV.text = binding!!.seekBarSB.progress.toString() + baseActivity!!.getString(R.string.km)

            }


            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {
                setCircle(seekBar?.progressFloat!!)
                binding!!.kmTV.text = binding!!.seekBarSB.progress.toString() + baseActivity!!.getString(R.string.km)

            }

            @SuppressLint("SetTextI18n")
            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                setCircle(seekBar?.progressFloat!!)
                binding!!.kmTV.text = binding!!.seekBarSB.progress.toString() + baseActivity!!.getString(R.string.km)
            }
        }
    }

    private fun removeisFirst() {
        if (isFirst) {
            isFirst = !isFirst
        }
    }

    private fun setCircle(progressFloat: Float) {

        if (circle != null) {
            circle!!.radius = 0.0
            circle!!.remove()
            circle = null
        }
        val radius: Double = progressFloat.toDouble() * 1000
        circle = googleMap!!.addCircle(CircleOptions()
                .center(LatLng(addressData!!.latitude?.toDouble()
                        ?: 0.0, addressData!!.longitude?.toDouble()
                        ?: 0.0))
                .radius(radius)
                .strokeWidth(5f)
                .strokeColor(0x44ff0000)
                .fillColor(0x30ff0000))


        getZoomForMetersWide(LatLng(addressData!!.latitude?.toDouble()
                ?: 0.0, addressData!!.longitude?.toDouble()
                ?: 0.0), radius.toInt())
    }


    private fun getZoomForMetersWide(latLngPoint: LatLng, desiredMeters: Int) {
        val metrics: DisplayMetrics = baseActivity!!.resources.displayMetrics
        val mapWidth = (binding!!.mapIV.width / 2) / metrics.density
        val EQUATOR_LENGTH = 20075016    //40075016    //20075016
        val TIME_ANIMATION_MILIS = 1500
        val latitudinalAdjustment = cos(Math.PI * latLngPoint.latitude / 180.0)
        val arg = EQUATOR_LENGTH * mapWidth * latitudinalAdjustment / (desiredMeters * 256.0)
        val valToZoom = ln(arg) / ln(2.0)

        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngPoint, valToZoom.toString().toFloat()), TIME_ANIMATION_MILIS, null)
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

    override fun onPlacesAddressFound(addressData: AddressData?) {
        baseActivity?.runOnUiThread {
            if (baseActivity!!.getAddressData() != null && isFirst) {
                removeisFirst()
                this.addressData = baseActivity!!.getAddressData()
                if (baseActivity!!.getAddressData() != null) {
                    if (baseActivity!!.getAddressData()!!.isZoomMap) {
                        getData = false
                        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(baseActivity!!.getAddressData()!!.latitude?.toDouble()
                                ?: 0.0, baseActivity!!.getAddressData()!!.longitude?.toDouble()
                                ?: 0.0), 15f))

                    }
                    binding!!.locationET.setText(baseActivity!!.getAddressData()!!.city)
                }

            } else {
                this.addressData = addressData
                if (addressData != null) {
                    if (addressData.isZoomMap) {
                        getData = false
                        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(addressData.latitude?.toDouble()
                                ?: 0.0, addressData.longitude?.toDouble() ?: 0.0), 15f))
                    }
                    binding!!.locationET.setText(addressData.city)
                }
            }
            if (googleMap != null) {
                binding!!.markerIV.visibility = View.VISIBLE
            } else {
                binding!!.markerIV.visibility = View.GONE
            }

            setCircle(binding!!.seekBarSB.progress.toFloat())
        }

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
//                        showToastOne(getString(R.string.not_able_to_get_latlong))
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
                val place = Autocomplete.getPlaceFromIntent(data)
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

    }

    private fun resetList() {
        list.clear()
        adapter = null
        isSingle = false
        pageCount = 0
    }

    private fun hitLocationListAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetTransportationList(pageCount)
            restFullClient!!.sendRequest(call, this)
        }
    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = AdapterTransportationLocation(baseActivity!!, list)
            binding!!.searchRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
            adapter!!.setOnItemClickListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.doneTV -> {
                if (binding!!.checkCB.isChecked) {
                    if (addressData != null) {
                        hitAddTransportationApi()
                    }
                }

            }
            R.id.confirnTV -> {
                if (binding!!.checkCB.isChecked) {
                    if (list.isNotEmpty()) {
                        setConfirmButton()
                    }
                } else {
                    setConfirmButton()
                }
            }
            R.id.locationET -> {
                baseActivity!!.hideSoftKeyboard()
                if (baseActivity!!.checkPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), Const.PLAY_SERVICES_RESOLUTION_REQUEST, this)) {
                    searchLocation(Const.PLAY_SERVICES_RESOLUTION_REQUEST)
                }
            }
        }
    }

    private fun setConfirmButton() {

        if (baseActivity is LoginSignUpActivity) {
            requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, VerifiedFragment())
                    .commit()
        } else {
            if (baseActivity!!.getProfileData()!!.isLocation && baseActivity!!.getProfileData()!!.isService && baseActivity!!.getProfileData()!!.isSubscription && baseActivity!!.getProfileData()!!.isAvailable && baseActivity!!.getProfileData()!!.isLanguage) {
                requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, MyBusinessSettingsFragment())
                        .commit()
            } else {
                requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                requireActivity().supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.container, VerifiedFragment())
                        .commit()
            }
        }

    }


    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_TRANSPORTATION_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    pageCount++
                    val totalId = jsonobject.getJSONObject(Const._META).getInt(Const.PAGE_COUNT)
                    isSingle = pageCount >= totalId
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<UserAddressListModel>(object1.toString(), UserAddressListModel::class.java)
                        list.add(data)
                        googleMap?.addMarker(MarkerOptions().position(LatLng(data!!.latitude?.toDouble()
                                ?: 0.0, data.longitude?.toDouble()
                                ?: 0.0)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_locationtodayyy)))
                    }

                    if (list.isNotEmpty()) {
                        binding!!.confirnTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.mid_blue_button)
                        setAdapter()
                        binding!!.searchRV.visibility = View.VISIBLE
                    } else {
                        binding!!.confirnTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.lightgrey_button)
                        binding!!.searchRV.visibility = View.GONE
                    }
                } else {
                    isSingle = false
                }
            } else if (responseUrl.contains(Const.API_TRANSPORTATION_DELETE)) {
                if (responseCode == Const.STATUS_OK) {
                    list.removeAt(pos)
                    adapter!!.notifyDataSetChanged()
                    if (list.isNotEmpty()) {
                        binding!!.confirnTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.mid_blue_button)
                        binding!!.searchRV.visibility = View.VISIBLE
                    } else {
                        binding!!.confirnTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.lightgrey_button)
                        binding!!.searchRV.visibility = View.GONE
                    }

                    googleMap?.clear()
                    for (i in list) {
                        googleMap?.addMarker(MarkerOptions().position(LatLng(i.latitude?.toDouble()
                                ?: 0.0, i.longitude?.toDouble()
                                ?: 0.0)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_locationtodayyy)))
                    }

                }
            } else if (responseUrl.contains(Const.API_TRANSPORTATION_ADD)) {
                if (responseCode == Const.STATUS_OK) {
                    val data = Gson().fromJson<UserAddressListModel>(jsonobject.getJSONObject("detail").toString(), UserAddressListModel::class.java)
                    list.add(data)
                    if (list.size > 1) {
                        adapter!!.notifyDataSetChanged()
                    } else {
                        setAdapter()
                    }
                    if (list.isNotEmpty()) {
                        binding!!.confirnTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.mid_blue_button)
                        binding!!.searchRV.visibility = View.VISIBLE
                    } else {
                        binding!!.confirnTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.lightgrey_button)
                        binding!!.searchRV.visibility = View.GONE
                    }
                    googleMap?.addMarker(MarkerOptions().position(LatLng(data!!.latitude?.toDouble()
                            ?: 0.0, data.longitude?.toDouble()
                            ?: 0.0)).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_locationtodayyy)))
                }
            } else if (responseUrl.contains(Const.API_UPDATE_PROFILE)) {
                if (responseCode == Const.STATUS_OK) {
                    try {
                        val data = Gson().fromJson<ProfileData>(jsonobject.getJSONObject("detail").toString(), ProfileData::class.java)
                        baseActivity!!.setProfileData(data)
                    } catch (e: Exception) {
                        baseActivity!!.handleException(e)
                    }
                }

            }

        } catch (e: Exception) {
            handleException(e)
        }
    }

    override fun onPageEnd(vararg itemData: Any) {
        hitLocationListAPI()
    }

    private var pos = 0
    private var mLastClickTime = SystemClock.elapsedRealtime()

    override fun onItemClick(vararg itemData: Any) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        pos = itemData[0] as Int

        AlertDialog.Builder(baseActivity!!,R.style.animateDialog)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.do_you_wantto_delete_location))
                .setPositiveButton(getString(R.string.yes)) { d, i ->
                    hitDeleteAPI(list[pos])
                    d.dismiss()
                }
                .setNegativeButton(getString(R.string.no)) { d, i ->
                    d.dismiss()
                }.show()

    }

    private fun hitDeleteAPI(categoryData: UserAddressListModel) {
        val call = api!!.apiTransportationDelete(categoryData.id!!)
        restFullClient!!.sendRequest(call, this)

    }

    private fun hitAddTransportationApi() {
        val param = Api3Params()
        param.put("Transpotation[km]", binding!!.seekBarSB.progress)
        param.put("Transpotation[longitude]", (addressData?.longitude ?: 0.0).toString())
        param.put("Transpotation[latitude]", (addressData?.latitude ?: 0.0).toString())
        param.put("Transpotation[address]", addressData?.address ?: "")
        param.put("Transpotation[city]", addressData?.city ?: "")
        val call = api!!.apiAddTransportation(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
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
}
