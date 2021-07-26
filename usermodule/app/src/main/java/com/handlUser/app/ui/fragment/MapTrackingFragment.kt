/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogTerminateServiceBinding
import com.handlUser.app.databinding.FragmentMapTrackingBinding
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.service.LocationUpdateService
import com.handlUser.app.ui.activity.BaseActivity
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.extensions.replaceFragmentWithoutStack
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.handlUser.app.utils.GoogleApisHandle
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MapTrackingFragment : BaseFragment(), ClickHandler, BaseActivity.PermCallback,
    GoogleApisHandle.OnPolyLineReceived, OnMapReadyCallback,
    LocationUpdateService.OnLocationReceived {

    private var cBinding: DialogTerminateServiceBinding? = null
    private var binding: FragmentMapTrackingBinding? = null

    private var data: AppointmentData? = null
    private var googleMap: GoogleMap? = null
    private var zoomValue = true
    private var driverLatitude: String? = null
    private var driverLongitude: String? = null
    private var providerLatitude: String? = null
    private var providerLongitude: String? = null
    private var currentLocation: Location? = null
    private var first: Boolean = false
    private var stateChange: Boolean = false
    private var timer: Timer? = null
    private var isProgressEnable: Boolean? = true
    override fun onSyncStart() {
        if (isProgressEnable!!) {
            super.onSyncStart()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_map_tracking, container, false
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
            data = arguments?.getParcelable<AppointmentData>("booking_data")
        }
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar("", ContextCompat.getColor(baseActivity!!, R.color.White), false)
        initializeMap()
        timer = Timer()
        if (data!!.provider.isTranspotation == Const.TYPE_OFF) {
            if (data!!.stateId == Const.STATE_ACCEPT || data!!.stateId == Const.STATE_START) {
                LocationUpdateService.getInstance().startService(baseActivity!!)
                baseActivity!!.locationService?.setLocationReceivedListener(this)
            }
        }

    }

    private fun getTime(): String {
        val simpleDateFormat = SimpleDateFormat("hh:mm a")

        val date1 = simpleDateFormat.parse(
            baseActivity!!.changeDateFormatGmtToLocal(
                data!!.bookingSlots[0].startTime,
                Const.DATE_FORMAT,
                "hh:mm a"
            )
        )
        val date2 = simpleDateFormat.parse(
            baseActivity!!.changeDateFormatFromDate(
                Calendar.getInstance().time,
                "hh:mm a"
            )
        )

        val difference: Long = date1!!.time - date2!!.time
        val days = (difference / (1000 * 60 * 60 * 24)).toInt()
        val hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
        val min =
            (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
        val hour = if (hours < 0) -hours else hours
        var time = ""

        if (days > 0) {
            time = "$days${baseActivity!!.getString(R.string.days)}"
        }
        if (hours > 0) {
            time = "$time$hours${baseActivity!!.getString(R.string.hours)}"
        }
        if (min > 0) {
            time = "$time$min${baseActivity!!.getString(R.string.minutes)}"
        }
        return time

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


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.startTV -> {
                when (data!!.stateId) {
                    Const.STATE_ACCEPT -> {
                        if (data!!.provider.isTranspotation != Const.TYPE_ON) {
                            hitChangeStateAPI(data!!.id, Const.STATE_START, "")
                        }
                    }
                    Const.STATE_START -> {
                        hitChangeStateAPI(data!!.id, Const.STATE_STOP, "")
                    }

                }

            }
            R.id.callTV -> {
                if (baseActivity!!.checkPermissions(
                        arrayOf(Manifest.permission.CALL_PHONE),
                        12,
                        this
                    )
                ) {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + data!!.provider.contactNo)
                    if (ActivityCompat.checkSelfPermission(
                            baseActivity!!,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    startActivity(intent)
                }

            }
            R.id.backIV -> {
                baseActivity!!.onBackPressed()
            }
            R.id.chatTV -> {
                val bundle = Bundle()
                bundle.putString("to_id", data!!.provider.id.toString())
                bundle.putString("model_id", data!!.id.toString())
                bundle.putString("to_name", data!!.provider.fullName)
                baseActivity!!.replaceFragment(ChatFragment(), bundle)
            }
            R.id.cancelTV -> {
                setCancelDialog()
            }
        }
    }

    private fun hitChangeStateAPI(id: Int, state: Int, desc: String) {
        isProgressEnable = true
        val call = api!!.apiChangeState(id, state, desc)
        restFullClient!!.sendRequest(call, this)
    }

    private fun setCancelDialog() {
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        cBinding = DataBindingUtil.inflate(
            LayoutInflater.from(baseActivity),
            R.layout.dialog_terminate_service,
            null,
            false
        )
        mBuilder.setView(cBinding!!.root)
        val alertDialog = mBuilder.create()
        alertDialog.show()

        cBinding!!.yesTV.setOnClickListener {
            alertDialog.dismiss()
            val call = api!!.apiChangeState(data!!.id, Const.STATE_TERMINATE, "")
            restFullClient!!.sendRequest(call, this)
        }

        cBinding!!.noTV.setOnClickListener {
            alertDialog.dismiss()
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
        baseActivity!!.googleApiHandle?.setPolyLineReceivedListener(this)
        googleMap!!.uiSettings.isMapToolbarEnabled = false
        if (googleMap != null) {
            resetUIStateChange()
        }
    }

    override fun permGranted(resultCode: Int) {
        if (resultCode == 140) {
            initializeMap()
        } else if (resultCode == 12) {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:" + data!!.provider.contactNo)
            if (ActivityCompat.checkSelfPermission(
                    baseActivity!!,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            startActivity(intent)
        } else {
            initializeMap()
        }
    }

    override fun permDenied(resultCode: Int) {

    }

    override fun onPolyLineReceived(
        origin: LatLng?,
        destination: LatLng?,
        routeMap: GoogleMap,
        totalDistance: String?,
        totalDuration: String?
    ) {
        setDestination(origin, destination)

    }


    override fun onSyncSuccess(
        responseCode: Int,
        responseMessage: String,
        responseUrl: String,
        response: String?
    ) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            if (responseCode == Const.STATUS_OK) {
                val jsonobject = JSONObject(response!!)
                if (responseUrl.contains(Const.API_AVAIL_CHANGE_STATE)) {
                    if (responseCode == Const.STATUS_OK) {
                        showToastOne(jsonobject.optString("message"))
                        data!!.stateId = jsonobject.optInt("state_id")
                        resetUIStateChange()
                        if (data!!.stateId == Const.STATE_END_WORK) {
                            val bundle = Bundle()
                            bundle.putParcelable("booking_data", data)
                            baseActivity!!.replaceFragmentWithoutStack(
                                OrderSummaryFragment(),
                                R.id.container,
                                bundle
                            )
                        } else if (data!!.stateId == Const.STATE_TERMINATE) {
                            baseActivity!!.onBackPressed()
                        } else if (data!!.stateId == Const.STATE_START) {
                            stateChange = true
                        } else if (data!!.stateId == Const.STATE_STOP) {
                            stateChange = true
                            LocationUpdateService.getInstance().stopService(baseActivity!!)
                        }
                    }
                } else if (responseUrl.contains(Const.API_GET_CURRENT_LOCATION)) {
                    if (responseCode == Const.STATUS_OK) {
                        if (data!!.stateId == Const.STATE_START && jsonobject.optInt("state_id") == Const.STATE_STOP) {
                            stateChange = true
                        } else if (data!!.stateId == Const.STATE_ACCEPT && jsonobject.optInt("state_id") == Const.STATE_START) {
                            stateChange = true
                        }
                        data!!.stateId = jsonobject.optInt("state_id")
                        if (data!!.provider.isTranspotation == Const.TYPE_ON) {
                            driverLatitude = jsonobject.optString("latitude")
                            driverLongitude = jsonobject.optString("longitude")
                            setURL()
                        } else {
                            resetUIStateChange()
                            providerLatitude = jsonobject.optString("latitude")
                            providerLongitude = jsonobject.optString("longitude")
                        }
                    }

                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun stopLocationData() {
        if (data!!.provider.isTranspotation == Const.TYPE_OFF) {
            LocationUpdateService.getInstance().stopService(baseActivity!!)
        }
    }

    private fun getLocationData() {
        if (data!!.provider.isTranspotation == Const.TYPE_OFF) {
            LocationUpdateService.getInstance().startService(baseActivity!!)
            baseActivity!!.locationService?.setLocationReceivedListener(this)
        }
    }

    private fun hitgetTrackingAPI() {
        val call = api!!.apiGetCurrentLocation(data!!.provider.id, data!!.id)
        restFullClient!!.sendRequest(call, this)
    }

    override fun onDestroyView() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        LocationUpdateService.getInstance().stopService(baseActivity!!)
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
        LocationUpdateService.getInstance().stopService(baseActivity!!)
        super.onDestroy()
    }

    override fun onLocationReceived(location: Location?) {
        this.currentLocation = location
        if (data!!.provider.isTranspotation == Const.TYPE_OFF) {
            driverLatitude = currentLocation!!.latitude.toString()
            driverLongitude = currentLocation!!.longitude.toString()
        }
        if (googleMap != null) {
            setURL()
        }

    }

    private fun resetUIStateChange() {
        val time: String = getTime()
        getDriverLocation()

        if (data!!.provider.isTranspotation == Const.TYPE_ON) {
            // watiting
            when (data!!.stateId) {
                Const.STATE_PENDING -> {
                    binding!!.titleTV.text = baseActivity!!.getString(R.string.pending_text)
                    binding!!.startTV.visibility = View.GONE
                }
                Const.STATE_ACCEPT -> {
                    binding!!.titleTV.text =
                        data!!.provider.firstName + baseActivity!!.getString(R.string.accept_text) + " " + time
                    binding!!.startTV.visibility = View.GONE

                }

                Const.STATE_START_WORK -> {
                    binding!!.startTV.visibility = View.GONE
                    binding!!.titleTV.text =
                        data!!.provider.firstName + getString(R.string.work_location)
                }
                Const.STATE_START -> {
                    binding!!.startTV.visibility = View.GONE
                    binding!!.titleTV.text =
                        data!!.provider.firstName + " " + baseActivity!!.getString(R.string.moving_provider)

                }
                Const.STATE_STOP -> {
                    binding!!.startTV.visibility = View.GONE
                    binding!!.titleTV.text =
                        baseActivity!!.getString(R.string.location1) + data!!.provider.firstName + baseActivity!!.getString(
                            R.string.location2
                        )
                }
                else -> {
                    binding!!.startTV.visibility = View.GONE
                }
            }


        } else {
            // pick her
            when (data!!.stateId) {
                Const.STATE_PENDING -> {
                    binding!!.titleTV.text = baseActivity!!.getString(R.string.pending_text)
                }
                Const.STATE_ACCEPT -> {
                    binding!!.titleTV.text =
                        data!!.provider.firstName + baseActivity!!.getString(R.string.accept_text) + " " + time + baseActivity!!.getString(
                            R.string.accept_text1
                        )
                    binding!!.startTV.visibility = View.VISIBLE
                    binding!!.startTV.text = baseActivity!!.getString(R.string.on_my_way)
                }

                Const.STATE_START_WORK -> {
                    binding!!.startTV.visibility = View.GONE
                    binding!!.titleTV.text =
                        data!!.provider.firstName + getString(R.string.work_location)
                }

                Const.STATE_START -> {
                    binding!!.startTV.visibility = View.VISIBLE
                    binding!!.startTV.text = baseActivity!!.getString(R.string.drop_provider)
                    binding!!.titleTV.text = baseActivity!!.getString(R.string.moving_provider)
                }
                Const.STATE_STOP -> {
                    binding!!.startTV.visibility = View.GONE
                    binding!!.titleTV.text =
                        baseActivity!!.getString(R.string.location1) + baseActivity!!.getString(R.string.location3) + data!!.provider.firstName + baseActivity!!.getString(
                            R.string.location4
                        )
                }
                else -> {
                    binding!!.startTV.visibility = View.GONE
                    binding!!.startTV.text = baseActivity!!.getString(R.string.start_task)
                }
            }

        }

    }

    private fun setURL() {
        resetUIStateChange()
        if (data!!.provider.isTranspotation == Const.TYPE_ON) {
            // watiting
            when (data!!.stateId) {
                Const.STATE_PENDING, Const.STATE_ACCEPT, Const.STATE_EDIT -> {
                    baseActivity!!.googleApiHandle!!.getDirectionsUrl(
                        LatLng(
                            data!!.provider.latitude.toDouble(),
                            data!!.provider.longitude.toDouble()
                        ),
                        LatLng(data!!.latitude!!.toDouble(), data!!.longitude.toDouble()),
                        googleMap!!,
                        zoomValue
                    )
                }


                Const.STATE_START_WORK, Const.STATE_START, Const.STATE_STOP -> {
                    baseActivity!!.googleApiHandle?.getDirectionsUrl(
                        LatLng(
                            driverLatitude?.toDouble()
                                ?: data!!.provider.latitude.toDouble(), driverLongitude?.toDouble()
                                ?: data!!.provider.longitude.toDouble()
                        ),
                        LatLng(data!!.latitude.toDouble(), data!!.longitude.toDouble()),
                        googleMap!!,
                        zoomValue
                    )
                }


            }


        } else {
            // pick her
            when (data!!.stateId) {
                Const.STATE_PENDING, Const.STATE_ACCEPT, Const.STATE_EDIT -> {
                    if (data!!.latitude.isNotBlank() && data!!.longitude.isNotBlank()) {
                        baseActivity!!.googleApiHandle?.getDirectionsUrl(
                            LatLng(
                                data!!.latitude.toDouble(),
                                data!!.longitude.toDouble()
                            ),
                            LatLng(
                                data!!.provider.latitude.toDouble(),
                                data!!.provider.longitude.toDouble()
                            ),
                            googleMap!!,
                            zoomValue
                        )

                    }
                }


                Const.STATE_START_WORK, Const.STATE_START, Const.STATE_STOP -> {
                    if (data!!.latitude.isNotBlank() && data!!.longitude.isNotBlank()) {

                        baseActivity!!.googleApiHandle?.getDirectionsUrl(
                            LatLng(
                                driverLatitude?.toDouble()
                                    ?: data!!.latitude.toDouble(), driverLongitude?.toDouble()
                                    ?: data!!.longitude.toDouble()
                            ), LatLng(
                                providerLatitude?.toDouble()
                                    ?: data!!.provider.latitude.toDouble(),
                                providerLongitude?.toDouble()
                                    ?: data!!.provider.longitude.toDouble()
                            ), googleMap!!, zoomValue
                        )
                    }
                }


            }

        }
        zoomValue = false

    }

    private fun getDriverLocation() {
        val delay: Int
        if (first) {
            first = false
            delay = Const.DELAY_ONE_TIMEOUT
        } else {
            delay = Const.DELAY_TIMEOUT
        }

        if (timer != null) {
            timer!!.schedule(object : TimerTask() {
                override fun run() {
                    baseActivity!!.runOnUiThread {
                        isProgressEnable = false
                        hitgetTrackingAPI()
                    }
                }
            }, delay.toLong())
        }

    }


    private var driverMarker: Marker? = null
    private var marker: Marker? = null

    private fun setDestination(origin: LatLng?, destination: LatLng?) {
        if (marker == null) {

            if (data!!.provider.isTranspotation == Const.TYPE_ON) {
                if (destination?.latitude != 0.0 && destination?.longitude != 0.0) {
                    marker = googleMap?.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                destination!!.latitude,
                                destination.longitude
                            )
                        ).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ghr))
                    )
                }
            } else {
                if (destination?.latitude != 0.0 && destination?.longitude != 0.0) {
                    marker = googleMap?.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                destination!!.latitude,
                                destination.longitude
                            )
                        ).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_locationtodayyy))
                    )
                }
            }
        }


        if (driverMarker == null) {
            if (origin?.latitude != null && origin?.longitude != null) {
                driverMarker = getMarker(origin)
            }

        } else {
            if (stateChange) {
                stateChange = false
                driverMarker?.remove()
                if (origin?.latitude != null && origin?.longitude != null) {
                    driverMarker = getMarker(origin)
                }
            }
            baseActivity!!.animateMarker(
                LatLng(
                    origin?.latitude ?: 0.0, origin?.longitude
                        ?: 0.0
                ), driverMarker!!, googleMap!!
            )
        }

        if (origin?.latitude != null && origin?.longitude != null) {
            val cameraPosition = CameraPosition.builder()
                .target(LatLng(origin.latitude, origin.longitude))
                .zoom(16f)
                .bearing(30f)
                .tilt(45f)
                .build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    private fun getMarker(origin: LatLng): Marker? {
        return if (data!!.provider.isTranspotation == Const.TYPE_ON) {
            if (data!!.stateId == Const.STATE_ACCEPT || data!!.stateId == Const.STATE_PENDING || data!!.stateId == Const.STATE_START_WORK || data!!.stateId == Const.STATE_STOP) {
                googleMap?.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            origin.latitude,
                            origin.longitude
                        )
                    ).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_locationtodayyy))
                )
            } else {
                googleMap?.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            origin.latitude,
                            origin.longitude
                        )
                    ).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car))
                )
            }
        } else {
            if (data!!.stateId == Const.STATE_ACCEPT || data!!.stateId == Const.STATE_PENDING || data!!.stateId == Const.STATE_START_WORK || data!!.stateId == Const.STATE_STOP) {
                googleMap?.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            origin.latitude,
                            origin.longitude
                        )
                    ).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ghr))
                )
            } else {
                googleMap?.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            origin.latitude,
                            origin.longitude
                        )
                    ).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car))
                )
            }

        }
    }


}
