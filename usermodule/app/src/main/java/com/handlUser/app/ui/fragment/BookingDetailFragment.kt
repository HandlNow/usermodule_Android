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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.baoyachi.stepview.bean.StepBean
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogTerminateServiceBinding
import com.handlUser.app.databinding.FragmentBookingDetailBinding
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.model.AvailabilitySlot
import com.handlUser.app.model.UserSubservices
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject
import java.util.*


class BookingDetailFragment : BaseFragment(), ClickHandler, OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var binding: FragmentBookingDetailBinding? = null
    private var data: AppointmentData? = null
    private var isExpanded: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            data = it?.getParcelable("data")
        }
    }

    private fun hitOrderSummaryAPI() {
        val call = api!!.apiGetOrderSummary(data!!.id)
        restFullClient!!.sendRequest(call, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_booking_detail, container, false
        )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private val stepsBeanList: MutableList<StepBean> = ArrayList()

    private fun initUI() {
        stepsBeanList.clear()
        binding!!.handleClick = this
        setData()

        binding!!.pullToRefresh.setOnRefreshListener {
            //  data = null
            hitOrderSummaryAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }
        initializeMap()
        when (data!!.stateId) {
            Const.STATE_CANCEL, Const.STATE_REJECT, Const.STATE_PENDING -> {
                binding!!.progressCL.visibility = View.GONE
            }
            else -> {
                binding!!.progressCL.visibility = View.VISIBLE
                hitgetTrackingAPI()
            }
        }

        val stepBean0 = StepBean(baseActivity!!.getString(R.string.task_nbooked), -1)
        val stepBean1 = StepBean(baseActivity!!.getString(R.string.on_your_nway_to_nclient), -1)
        val stepBean2 = StepBean(baseActivity!!.getString(R.string.task_nstarted), -1)
        val stepBean3 = StepBean(baseActivity!!.getString(R.string.task_ncompleted), -1)
        val stepBean4 = StepBean(baseActivity!!.getString(R.string.review_nand_npayment), -1)
        stepsBeanList.add(stepBean0)
        stepsBeanList.add(stepBean1)
        stepsBeanList.add(stepBean2)
        stepsBeanList.add(stepBean3)
        stepsBeanList.add(stepBean4)

        binding!!.progressCL
            .setStepViewTexts(stepsBeanList)
            .setTextSize(12)
            .setStepsViewIndicatorCompletedLineColor(
                ContextCompat.getColor(
                    baseActivity!!,
                    R.color.light_blue
                )
            )
            .setStepsViewIndicatorUnCompletedLineColor(
                ContextCompat.getColor(
                    baseActivity!!,
                    R.color.grey_check
                )
            ).setStepViewComplectedTextColor(
                ContextCompat.getColor(
                    baseActivity!!,
                    R.color.sub_sentence_color
                )
            )
            .setStepViewUnComplectedTextColor(
                ContextCompat.getColor(
                    baseActivity!!,
                    R.color.sub_sentence_color
                )
            )
            .setStepsViewIndicatorCompleteIcon(
                ContextCompat.getDrawable(
                    baseActivity!!,
                    R.drawable.ic_baseline_radio_button_checked_24
                )
            )
            .setStepsViewIndicatorDefaultIcon(
                ContextCompat.getDrawable(
                    baseActivity!!,
                    R.drawable.ic_light_grey_circle
                )
            )
            .setStepsViewIndicatorAttentionIcon(
                ContextCompat.getDrawable(
                    baseActivity!!,
                    R.drawable.ic_light_blue_circle
                )
            )

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

    @SuppressLint("SetTextI18n")
    private fun setData() {
        when (data?.stateId) {
            Const.STATE_PENDING -> {
                baseActivity!!.setToolbar(
                    title = getString(R.string.new_book_req) + " (" + baseActivity!!.getString(
                        R.string.ref
                    ) + data?.id.toString() + ")",
                    screenBg = ContextCompat.getColor(baseActivity!!, R.color.White)
                )
                binding!!.timeTV.text = ""
            }
            else -> {
                baseActivity!!.setToolbar(
                    title = data?.userService?.categoryName + " " + baseActivity!!.getString(
                        R.string.ref
                    ) + data?.id.toString(),
                    screenBg = ContextCompat.getColor(baseActivity!!, R.color.White)
                )
            }
        }

        binding!!.catNameTV.text =
            data!!.userService.categoryName + " ( ref:" + data!!.id.toString() + ")"
        binding!!.servicesTV.text = getMultiId(data!!.userService.userSubservices)
        binding!!.reqTV.text = data!!.providerDescription
        binding!!.userIV.loadFromUrl(baseActivity!!, data!!.provider.profileFile!!)
        val date = baseActivity!!.changeDateFormatGmtToLocal(
            data!!.bookingSlots[0].startTime,
            Const.DATE_FORMAT,
            "EEEE - dd MMMM yyyy"
        )
        val time = baseActivity!!.changeDateFormatGmtToLocal(
            data!!.bookingSlots[0].startTime,
            Const.DATE_FORMAT,
            "hh:mm a"
        ) + " - " +
                baseActivity!!.changeDateFormatGmtToLocal(
                    data!!.bookingSlots[data!!.bookingSlots.size - 1].endTime,
                    Const.DATE_FORMAT,
                    "hh:mm a"
                )

        binding!!.addressTV.text = data!!.address
        binding!!.nameTV.text = data!!.provider.firstName

        binding!!.dateTV.text = date
        binding!!.slotTV.text =
            time + " (" + (30 * data!!.bookingSlots.size).toString() + baseActivity!!.getString(R.string.minss)
        when (data!!.stateId) {
            Const.STATE_PENDING -> {
                resetVisibilityButton()
                binding!!.cancelTV.visibility = View.VISIBLE
            }
            Const.STATE_ACCEPT -> {
                resetVisibilityButton()
                binding!!.seeLocationTV.visibility = View.VISIBLE
                binding!!.chatTV.visibility = View.VISIBLE
                binding!!.progressTV.visibility = View.GONE
                binding!!.cancelTV.visibility = View.VISIBLE
                if (data!!.provider.isTranspotation != Const.TYPE_ON) {
                    binding!!.onMyWayTV.visibility = View.VISIBLE
                }
            }
            Const.STATE_CANCEL -> {
                resetVisibilityButton()
                binding!!.cancelledTV.visibility = View.VISIBLE
                binding!!.terminateTV.visibility = View.GONE
                binding!!.timeTV.text = ""
            }
            Const.STATE_REJECT -> {
                resetVisibilityButton()
                binding!!.rejectedTV.visibility = View.VISIBLE
                binding!!.timeTV.text = ""
            }

            Const.STATE_START_WORK, Const.STATE_STOP-> {
                resetVisibilityButton()
                binding!!.seeLocationTV.visibility = View.VISIBLE
                binding!!.chatTV.visibility = View.VISIBLE
                binding!!.progressTV.visibility = View.GONE
                binding!!.terminateTV.visibility = View.VISIBLE
            }
           Const.STATE_START -> {
                resetVisibilityButton()
                binding!!.seeLocationTV.visibility = View.VISIBLE
                binding!!.chatTV.visibility = View.VISIBLE
                binding!!.progressTV.visibility = View.GONE
                binding!!.terminateTV.visibility = View.VISIBLE
               if (data!!.provider.isTranspotation != Const.TYPE_ON) {
                   binding!!.onMyWayTV.visibility = View.VISIBLE
                   binding!!.onMyWayTV.text =baseActivity!!.getString(R.string.drop_provider)
               }
           }

        }


    }

    private fun resetVisibilityButton() {
        binding!!.seeLocationTV.visibility = View.GONE
        binding!!.chatTV.visibility = View.GONE
        binding!!.progressTV.visibility = View.GONE
        binding!!.rejectedTV.visibility = View.GONE
        binding!!.cancelledTV.visibility = View.GONE
        binding!!.terminateTV.visibility = View.GONE
        binding!!.cancelTV.visibility = View.GONE
        binding!!.onMyWayTV.visibility = View.GONE
    }

    private fun getMultiId(colorList: ArrayList<UserSubservices>): String {
        var id = ""
        for (data in colorList) {
            id += data.parentName + "\n"
        }
        return when {
            id.isNotEmpty() -> id.trim().substring(0, id.length - 1)
            else -> id
        }
    }


    override fun onHandleClick(view: View) {
        when (view.id) {

            R.id.cancelTV -> {
                AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
                    .setMessage(baseActivity!!.getString(R.string.cancel_sure))
                    .setPositiveButton(getString(R.string.confirm)) { d, i ->
                        hitAcceptRejectAPI(data!!.id, Const.STATE_CANCEL)
                        d.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { d, i ->
                        d.dismiss()
                    }.show()

            }
            R.id.chatTV -> {
                val bundle = Bundle()
                bundle.putString("to_id", data!!.provider.id.toString())
                bundle.putString("model_id", data!!.id.toString())
                bundle.putString("to_name", data!!.provider.fullName)
                baseActivity!!.replaceFragment(ChatFragment(), bundle)

            }
            R.id.terminateTV -> {
                setCancelDialog()
            }
            R.id.onMyWayTV -> {
                when (data!!.stateId) {
                    Const.STATE_ACCEPT -> {
                        if (data!!.provider.isTranspotation != Const.TYPE_ON) {
                            hitAcceptRejectAPI(data!!.id, Const.STATE_START)
                        }
                    }
                    Const.STATE_START -> {
                        hitAcceptRejectAPI(data!!.id, Const.STATE_STOP)
                    }

                }

            }
            R.id.seeLocationTV -> {
                val bundle = Bundle()
                bundle.putParcelable("booking_data", data)
                val calender = Calendar.getInstance()
                var timeCalender = calender.time
                var time = baseActivity!!.changeDateFormatFromDate(timeCalender, Const.DATE_FORMAT)
                if (baseActivity!!.changeDateFormatGmtToLocal(
                        data!!.bookingSlots[0].startTime,
                        Const.DATE_FORMAT,
                        Const.DATE_FORMAT
                    ) <= time && baseActivity!!.changeDateFormatGmtToLocal(
                        data!!.bookingSlots[data!!.bookingSlots.size - 1].endTime,
                        Const.DATE_FORMAT,
                        Const.DATE_FORMAT
                    ) > time
                ) {
                    baseActivity!!.replaceFragment(MapTrackingFragment(), bundle)
                } else {
                    showToast("you cannot start now")
                }
            }
            R.id.progressTV -> {
                val bundle = Bundle()
                bundle.putParcelable("booking_data", data)
                baseActivity!!.replaceFragment(TrackingProgressFragment(), bundle)
            }

            R.id.expandTV -> {
                if (isExpanded) {
                    isExpanded = false
                    val height = 300
                    val parms =
                        ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                    binding!!.mapIV.setLayoutParams(parms)
                    binding!!.expandTV.rotation = 0f

                } else {
                    isExpanded = true
                    val height = 750
                    val parms =
                        ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                    binding!!.mapIV.setLayoutParams(parms)
                    binding!!.expandTV.rotation = 180f

                }
            }

        }
    }

    private var cBinding: DialogTerminateServiceBinding? = null
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

    private fun hitgetTrackingAPI() {
        val call = api!!.apiGetTracking(data!!.id)
        restFullClient!!.sendRequest(call, this)
    }

    private fun hitAcceptRejectAPI(id: Int, state: Int) {
        val call = api!!.apiChangeState(id, state, "")
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(
        responseCode: Int,
        responseMessage: String,
        responseUrl: String,
        response: String?
    ) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_ORDER_SUMMARY)) {
                if (responseCode == Const.STATUS_OK) {
                    data = Gson().fromJson(
                        jsonobject.getJSONObject("detail").toString(),
                        AppointmentData::class.java
                    )
                    setData()
                }
            } else if (responseUrl.contains(Const.API_AVAIL_CHANGE_STATE)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(jsonobject.optString("message"))
                    baseActivity!!.onBackPressed()
                }
            } else if (responseUrl.contains(Const.API_TRACKING_DATA)) {
                if (responseCode == Const.STATUS_OK) {
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<AvailabilitySlot>(
                            object1.toString(),
                            AvailabilitySlot::class.java
                        )

                        when (data.stateId) {
                            Const.STATE_ACCEPT -> {
                                stepsBeanList[0].state = 1
                                stepsBeanList[1].state = 0

                            }
                            Const.STATE_STOP, Const.STATE_START -> {
                                stepsBeanList[0].state = 1
                                stepsBeanList[1].state = 1
                                stepsBeanList[2].state = 0

                            }

                            Const.STATE_START_WORK -> {
                                stepsBeanList[0].state = 1
                                stepsBeanList[1].state = 1
                                stepsBeanList[2].state = 1
                                stepsBeanList[3].state = 0
                            }
                            Const.STATE_END_WORK -> {
                                binding!!.requestButtonCL.visibility = View.GONE
                                binding!!.timeTV.text =
                                    baseActivity!!.resources.getString(R.string.completed)
                                stepsBeanList[0].state = 1
                                stepsBeanList[1].state = 1
                                stepsBeanList[2].state = 1
                                stepsBeanList[3].state = 1
                                stepsBeanList[4].state = 0
                            }
                            Const.STATE_REVIEWED -> {
                                binding!!.requestButtonCL.visibility = View.GONE
                                binding!!.timeTV.text =
                                    baseActivity!!.resources.getString(R.string.completed)
                                stepsBeanList[0].state = 1
                                stepsBeanList[1].state = 1
                                stepsBeanList[2].state = 1
                                stepsBeanList[3].state = 1
                                stepsBeanList[4].state = 1
                            }

                        }
                        binding!!.progressCL.setStepViewTexts(stepsBeanList)
                    }

                }

            }


        } catch (e: Exception) {
            handleException(e)
        }
    }

    override fun permGranted(resultCode: Int) {
        initializeMap()
    }

    override fun permDenied(resultCode: Int) {

    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
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
        googleMap!!.uiSettings.isMapToolbarEnabled = false
        googleMap?.addMarker(
            MarkerOptions().position(
                LatLng(
                    data!!.latitude.toDouble(),
                    data!!.longitude.toDouble()
                )
            ).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_ghr))
        )
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    data!!.latitude.toDouble(),
                    data!!.longitude.toDouble()
                ), 20f
            )
        )
    }

}
