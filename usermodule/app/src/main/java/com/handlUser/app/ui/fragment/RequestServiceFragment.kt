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
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentRequestServiceBinding
import com.handlUser.app.model.AvailabilitySlot
import com.handlUser.app.model.LangData
import com.handlUser.app.model.ProviderData
import com.handlUser.app.ui.activity.MainActivity
import com.handlUser.app.ui.adapter.AdapterSlots
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.makeScrollableInsideScrollView
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.extensions.replaceFragmentWithoutStackWithoutBundle
import com.handlUser.app.ui.fragment.login.NotificationFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.handlUser.app.utils.GoogleApisHandle
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject


class RequestServiceFragment : BaseFragment(), ClickHandler, OnMapReadyCallback, GoogleApisHandle.OnPolyLineReceived {

    private var binding: FragmentRequestServiceBinding? = null
    private var googleMap: GoogleMap? = null
    private var googleApisHandle: GoogleApisHandle? = null
    private var zoomValue = true

    private var providerData: ProviderData? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_request_service, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            providerData = arguments?.getParcelable<ProviderData>("provider_data")
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUI() {
        setHasOptionsMenu(true)
        binding!!.handleClick = this
        baseActivity!!.setToolbar(providerData!!.fullName, ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.providerData = providerData

        binding!!.userIV.loadFromUrl(baseActivity!!, providerData!!.profileFile)
        if (providerData!!.isTranspotation == Const.TYPE_ON) {
            baseActivity!!.setTextViewDrawableColor(binding!!.dateTV, R.color.dark_blue)
            binding!!.locationTV.visibility = View.GONE
            binding!!.pickTV.visibility = View.GONE
            binding!!.mapFrame.visibility = View.GONE
        } else {
            baseActivity!!.setTextViewDrawableColor(binding!!.dateTV, R.color.carColor)
            binding!!.locationTV.visibility = View.VISIBLE
            binding!!.pickTV.visibility = View.VISIBLE
            binding!!.mapFrame.visibility = View.VISIBLE
        }
        val date = arguments?.getString("date", "")
        binding!!.picdateTV.text = baseActivity!!.changeDateFormat(date, "dd-MM-yyyy", "EEEE dd MMMM")
        binding!!.dateValueTV.text = baseActivity!!.changeDateFormat(date, "dd-MM-yyyy", "dd (EEEE) MMM")
        googleApisHandle = GoogleApisHandle.getInstance(baseActivity!!)

        binding!!.slotsRV.adapter = AdapterSlots(baseActivity!!, providerData!!.avalibility, this)
        initializeMap()
        binding!!.ratingTV.text = providerData!!.rating.toString()
        binding!!.nameTV.text = getMultiId(providerData!!.selecteLanguage)
        binding!!.nameTV.makeScrollableInsideScrollView()
        binding!!.slotsRV.setOnTouchListener { v, event ->
            baseActivity!!.hideSoftKeyboard()
            if (binding!!.slotsRV.hasFocus()) {
                v.parent.requestDisallowInterceptTouchEvent(true)
                if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_SCROLL) {
                    v.parent.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
            }
            false
        }
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
        when (view.id) {
            R.id.seeMoreTV -> {
                baseActivity!!.replaceFragment(SummaryFragment(), requireArguments())
            }
            R.id.requestTV -> {
                hitRequestAPI()

            }
        }
    }

    private fun hitRequestAPI() {
        var allWeek: List<AvailabilitySlot> = listOf()
        val list = providerData!!.avalibility
        for (i in 0 until list.size) {
            if (list[i].isChecked) {
                allWeek = list[i].availabilitySlots.filter { it.isChecked }
            }
        }
        if (allWeek.isNotEmpty()) {
            val params = Api3Params()
            params.put("Booking[provider_id]", providerData!!.id)
            params.put("Booking[user_service_id]", arguments?.getInt("service_id", 0) as Int)
            params.put("Booking[user_requirement_id]", arguments?.getInt("extra_id", 0) as Int)
            params.put("Booking[price]", (providerData!!.providerService[0].price.toFloat() / 2) * (allWeek.size))
            params.put("Booking[address]", baseActivity!!.store!!.getString(Const.HOME_ADD) as String)
            params.put("Booking[latitude]", baseActivity!!.store!!.getString(Const.HOME_LAT) as String)
            params.put("Booking[longitude]", baseActivity!!.store!!.getString(Const.HOME_LONG) as String)
            params.put("slot", getMultiId(allWeek))
            val call = api!!.apiReqServiceBooking(params.getServerHashMap())
            restFullClient!!.sendRequest(call, this)
        } else {
            showToastOne(getString(R.string.please_select_time_slot))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_noti, menu)
        val item = menu.findItem(R.id.notiIV)
        val countTV = item.actionView.findViewById<AppCompatTextView>(R.id.countTV)
        countTV.text = baseActivity!!.getProfileData()?.notificationCount.toString()
    }

    private fun getMultiId(allWeek: List<AvailabilitySlot>): String {

        var id = ""
        for (data in allWeek) {
            id = String.format("%s,%d", id, data.id)
        }

        return when {
            id.isNotEmpty() -> id.substring(1)
            else -> id
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.notiIV -> {

                baseActivity!!.replaceFragment(NotificationFragment())

            }
            R.id.menuIV -> {
                (baseActivity as MainActivity).openDrawer()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    fun onItemsetData() {
        var allWeek: List<AvailabilitySlot> = listOf()
        val list = providerData!!.avalibility
        for (i in 0 until list.size) {
            if (list[i].isChecked) {
                allWeek = list[i].availabilitySlots.filter { it.isChecked }
            }
        }

        if (allWeek.isNotEmpty()) {
            binding!!.startValueTV.text = baseActivity!!.changeDateFormatGmtToLocal(allWeek[0].startTime, Const.DATE_FORMAT, "hh:mm a")
            val count = allWeek.size
            if (count > 1) {
                binding!!.estimateValueTV.text = "30 " + baseActivity!!.getString(R.string.mins) + " - " + (30 * count).toString() + " " + baseActivity!!.getString(R.string.mins)
                binding!!.estimateCostValueTV.text = baseActivity!!.getString(R.string.euro) + (providerData!!.providerService[0].price.toFloat() / 2) + "-" + (providerData!!.providerService[0].price.toFloat() / 2) * count
            } else {
                binding!!.estimateValueTV.text = "0 - " + "30 " + baseActivity!!.getString(R.string.mins)
                binding!!.estimateCostValueTV.text = baseActivity!!.getString(R.string.euro) + (providerData!!.providerService[0].price.toFloat() / 2)
            }
        } else {
            binding!!.estimateValueTV.text = ""
            binding!!.estimateCostValueTV.text = ""
            binding!!.startValueTV.text = ""
        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_AVAIL_BOOKING)) {
                if (responseCode == Const.STATUS_OK) {
                    baseActivity!!.showToastOne("Request sent successfully")
                    baseActivity!!.replaceFragmentWithoutStackWithoutBundle(HomeFragment())
                }
            }

        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        this.googleMap = map
        if (ActivityCompat.checkSelfPermission(baseActivity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(baseActivity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        googleApisHandle?.setPolyLineReceivedListener(this)
        googleApisHandle?.getDirectionsUrl(LatLng(baseActivity!!.store!!.getString(Const.HOME_LAT)!!.toDouble(), baseActivity!!.store!!.getString(Const.HOME_LONG)!!.toDouble()), LatLng(providerData!!.latitude.toDouble(), providerData!!.longitude.toDouble()), googleMap!!, zoomValue)
        zoomValue = false
    }

    override fun onPolyLineReceived(origin: LatLng?, destination: LatLng?, routeMap: GoogleMap, totalDistance: String?, totalDuration: String?) {
        setDestination()
    }

    private fun setDestination() {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(baseActivity!!.store!!.getString(Const.HOME_LAT)!!.toDouble(), baseActivity!!.store!!.getString(Const.HOME_LONG)!!.toDouble()), 14f))

        if (providerData!!.latitude.toDouble() != 0.0 && providerData!!.longitude.toDouble() != 0.0) {
            googleMap?.addMarker(MarkerOptions().position(LatLng(providerData!!.latitude.toDouble(), providerData!!.longitude.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_locationtodayyy)))
        }
        if (baseActivity!!.store!!.getString(Const.HOME_LAT)!!.toDouble() != 0.0 && baseActivity!!.store!!.getString(Const.HOME_LONG)!!.toDouble() != 0.0) {
            googleMap?.addMarker(MarkerOptions().position(LatLng(baseActivity!!.store!!.getString(Const.HOME_LAT)!!.toDouble(), baseActivity!!.store!!.getString(Const.HOME_LONG)!!.toDouble())).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location)))
        }

    }

    private fun getMultiId(colorList: ArrayList<LangData>): String {
        var id = ""
        for (data in colorList) {
            id = id+ data.languageTitle + ","
        }
        return when {
            id.isNotEmpty() -> id.substring(0, id.length - 1)
            else -> id
        }
    }

}
