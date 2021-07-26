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
import android.app.DatePickerDialog
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
import com.baoyachi.stepview.bean.StepBean
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.AddTimeDialogBinding
import com.handlService.app.databinding.DialogEditRequestBinding
import com.handlService.app.databinding.FragmentBookingDetailBinding
import com.handlService.app.model.AppointmentData
import com.handlService.app.model.AvailabilitySlot
import com.handlService.app.model.SlotData
import com.handlService.app.model.UserSubservices
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.ui.adapter.AdapterAvailableSlots
import com.handlService.app.ui.extensions.loadFromUrl
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.extensions.setColor
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import kotlinx.android.synthetic.main.dialog_custom_cancel.view.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class BookingDetailFragment : BaseFragment(), ClickHandler, BaseActivity.PermCallback,
    OnMapReadyCallback {

    private var isExpanded: Boolean = false
    private var googleMap: GoogleMap? = null
    private var binding: FragmentBookingDetailBinding? = null
    private var data: AppointmentData? = null
    private val stepsBeanList: MutableList<StepBean> = ArrayList()

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

    private var date: DatePickerDialog.OnDateSetListener? = null
    private var myCalendar = Calendar.getInstance()
    private fun initUI() {
        stepsBeanList.clear()
        binding!!.handleClick = this
        setData()

        binding!!.pullToRefresh.setOnRefreshListener {
            hitOrderSummaryAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }

        initializeMap()
        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }
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

    private fun hitgetTrackingAPI() {
        val call = api!!.apiGetTracking(data!!.id)
        restFullClient!!.sendRequest(call, this)
    }

    private var localSlotDate = ""
    private var localSlotday = 0
    private fun updateLabel() {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        localSlotDate = sdf.format(myCalendar.time)
        localSlotday = baseActivity!!.dayOfWeek(localSlotDate)
        hitGetSlotAPI()

    }

    private fun hitGetSlotAPI() {
        val params = Api3Params()
        params.put("Availability[day_id]", localSlotday)
        params.put(
            "Availability[start_time]",
            baseActivity!!.changeDateFormatToGmt(
                localSlotDate + " " + Const.DATE_00_00_00,
                Const.DATE_FORMAT,
                Const.DATE_FORMAT
            )
        )
        params.put(
            "Availability[end_time]",
            baseActivity!!.changeDateFormatToGmt(
                localSlotDate + " " + Const.DATE_23_59_59,
                Const.DATE_FORMAT,
                Const.DATE_FORMAT
            )
        )
        val call = api!!.hitGetSlotAvailabilities(params.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
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

    private fun setData() {

        when (data?.stateId) {
            Const.STATE_PENDING -> {
                baseActivity!!.setToolbar(
                    title = getString(R.string.new_book_req) + " (" + baseActivity!!.getString(
                        R.string.ref
                    ) + data?.id.toString() + ")",
                    screenBg = ContextCompat.getColor(baseActivity!!, R.color.White)
                )
                binding!!.timeTV.text = baseActivity!!.getTIME(
                    SimpleDateFormat(Const.DATE_FORMAT).parse(
                        baseActivity!!.changeDateFormatGmtToLocal(
                            data!!.bookingSlots[0].startTime,
                            Const.DATE_FORMAT,
                            Const.DATE_FORMAT
                        )
                    )
                )
                binding!!.timeTV.setColor(baseActivity, R.color.Red)
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
        binding!!.reqTV.text = data!!.userDescription
        binding!!.userIV.loadFromUrl(baseActivity!!, data!!.userDetail!!.profileFile)
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
        binding!!.nameTV.text = data!!.userDetail!!.fullName

        binding!!.dateTV.text = date
        binding!!.slotTV.text =
            time + " (" + (30 * data!!.bookingSlots.size).toString() + baseActivity!!.getString(R.string.minss)

        when (data!!.stateId) {
            Const.STATE_EDIT -> {
                resetVisibilityButton()
                binding!!.declineTV.visibility = View.VISIBLE
                binding!!.editTaskTV.visibility = View.GONE
                binding!!.editTV.visibility = View.GONE
            }
            Const.STATE_PENDING -> {
                resetVisibilityButton()
                binding!!.acceptTV.visibility = View.VISIBLE
                binding!!.editTV.visibility = View.VISIBLE
                binding!!.declineTV.visibility = View.VISIBLE
            }
            Const.STATE_ACCEPT -> {
                resetVisibilityButton()
                binding!!.seeLocationTV.visibility = View.VISIBLE
                binding!!.chatTV.visibility = View.VISIBLE
                binding!!.progressTV.visibility = View.GONE
                if (data!!.typeId == 0) {
                    binding!!.editTaskTV.visibility = View.VISIBLE
                }
                binding!!.cancelTV.visibility = View.VISIBLE
                if (data!!.provider.isTranspotation == Const.TYPE_ON) {
                    binding!!.onMyWayTV.visibility = View.VISIBLE
                }
            }
            Const.STATE_CANCEL -> {
                resetVisibilityButton()
                binding!!.cancelledTV.visibility = View.VISIBLE
                binding!!.timeTV.text = ""
            }
            Const.STATE_REJECT -> {
                resetVisibilityButton()
                binding!!.rejectedTV.visibility = View.VISIBLE
                binding!!.timeTV.text = ""
            }

            Const.STATE_START_WORK -> {
                resetVisibilityButton()
                binding!!.seeLocationTV.visibility = View.VISIBLE
                binding!!.chatTV.visibility = View.VISIBLE
                binding!!.progressTV.visibility = View.GONE
                binding!!.progressTV.visibility = View.GONE
                binding!!.startTV.visibility = View.GONE
                binding!!.completeTV.visibility = View.VISIBLE
            }
            Const.STATE_STOP -> {
                resetVisibilityButton()
                binding!!.seeLocationTV.visibility = View.VISIBLE
                binding!!.chatTV.visibility = View.VISIBLE
                binding!!.progressTV.visibility = View.GONE
                binding!!.startTV.visibility = View.VISIBLE
            }
            Const.STATE_START -> {
                resetVisibilityButton()
                binding!!.seeLocationTV.visibility = View.VISIBLE
                binding!!.chatTV.visibility = View.VISIBLE
                binding!!.progressTV.visibility = View.GONE
                if (data!!.provider.isTranspotation == Const.TYPE_ON) {
                    binding!!.onMyWayTV.visibility = View.VISIBLE
                    binding!!.onMyWayTV.text = baseActivity!!.getString(R.string.drop_provider)
                }

            }

        }

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


    private fun resetVisibilityButton() {
        binding!!.acceptTV.visibility = View.GONE
        binding!!.editTV.visibility = View.GONE
        binding!!.editTaskTV.visibility = View.GONE
        binding!!.seeLocationTV.visibility = View.GONE
        binding!!.progressTV.visibility = View.GONE
        binding!!.declineTV.visibility = View.GONE
        binding!!.cancelTV.visibility = View.GONE
        binding!!.rejectedTV.visibility = View.GONE
        binding!!.onMyWayTV.visibility = View.GONE
        binding!!.cancelledTV.visibility = View.GONE
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.acceptTV -> {
                hitAcceptRejectAPI(data!!.id, Const.STATE_ACCEPT)

            }
            R.id.startTV -> {
                hitAcceptRejectAPI(data!!.id, Const.STATE_START_WORK)
            }
            R.id.onMyWayTV -> {
                when (data!!.stateId) {
                    Const.STATE_ACCEPT -> {
                        hitAcceptRejectAPI(data!!.id, Const.STATE_START)
                    }
                    Const.STATE_START -> {
                        hitAcceptRejectAPI(data!!.id, Const.STATE_STOP)

                    }
                }

            }
            R.id.completeTV -> {
                hitAcceptRejectAPI(data!!.id, Const.STATE_END_WORK)
            }
            R.id.crossIV -> {
                dialog!!.dismiss()
            }
            R.id.chatTV -> {
                val bundle = Bundle()
                bundle.putString("to_id", data!!.userDetail!!.id.toString())
                bundle.putString("model_id", data!!.id.toString())
                bundle.putString("to_name", data!!.userDetail!!.fullName)
                baseActivity!!.replaceFragment(ChatFragment(), bundle)
            }
            R.id.declineTV -> {
                AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
                    .setMessage(baseActivity!!.getString(R.string.reject_sure))
                    .setPositiveButton(getString(R.string.confirm)) { d, i ->
                        hitAcceptRejectAPI(data!!.id, Const.STATE_REJECT)
                        d.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { d, i ->
                        d.dismiss()
                    }.show()

            }
            R.id.cancelTV -> {
                hitCancelTrack()
                showCancelDialog()
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
                    ) <= time.toString() && baseActivity!!.changeDateFormatGmtToLocal(
                        data!!.bookingSlots[data!!.bookingSlots.size - 1].endTime,
                        Const.DATE_FORMAT,
                        Const.DATE_FORMAT
                    ) > time.toString()
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

            R.id.editTaskTV, R.id.editTV -> {
                AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
                    .setMessage(baseActivity!!.getString(R.string.edit_sure))
                    .setPositiveButton(getString(R.string.confirm)) { d, i ->
                        d.dismiss()
                        change()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { d, i ->
                        d.dismiss()
                    }.show()

            }
            R.id.expandTV -> {
                if (isExpanded) {
                    isExpanded = false
                    val height = 300
                    val parms =
                        ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
                    binding!!.expandTV.rotation = 0f
                    binding!!.mapIV.setLayoutParams(parms)
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

    private fun hitCancelTrack() {
        val call = api!!.apiCancelTrack(data!!.id)
        restFullClient!!.sendRequest(call, this)
    }

    fun showCancelDialog() {
        val mDialogView =
            LayoutInflater.from(baseActivity!!).inflate(R.layout.dialog_custom_cancel, null)
        val mBuilder = android.app.AlertDialog.Builder(baseActivity!!, R.style.transparentDialog)
            .setView(mDialogView)
        val dialog = mBuilder.create()
        dialog.show()
        mDialogView!!.yesTV.setOnClickListener {
            hitAcceptRejectAPI(data!!.id, Const.STATE_CANCEL)
            dialog?.dismiss()
        }
        mDialogView.noTV.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun change() {

        val dialog = DatePickerDialog(
            baseActivity!!, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
        dialog.show()

    }

    fun onRootCLick(position: Int) {
        dialog!!.dismiss()
        data!!.slotId = dayList[position]
        setEDitDialog()
    }

    private fun setEDitDialog() {

        val editBinding: DialogEditRequestBinding = DataBindingUtil.inflate(
            LayoutInflater.from(baseActivity),
            R.layout.dialog_edit_request,
            null,
            false
        )
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        mBuilder.setView(editBinding.root)
        val dialog = mBuilder.create()
        dialog.show()
        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        val date = baseActivity!!.changeDateFormatGmtToLocal(
            data!!.bookingSlots[0].startTime,
            Const.DATE_FORMAT,
            "EEE - MMM yyyy"
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
        editBinding.oldTimeValueTV.text = date + " (" + time + ")"
        val dates = baseActivity!!.changeDateFormatGmtToLocal(
            data!!.slotId!!.startTime,
            Const.DATE_FORMAT,
            "EEE - MMM yyyy"
        )
        val times = baseActivity!!.changeDateFormatGmtToLocal(
            data!!.slotId!!.startTime,
            Const.DATE_FORMAT,
            "hh:mm a"
        ) + " - " +
                baseActivity!!.changeDateFormatGmtToLocal(
                    data!!.slotId!!.endTime,
                    Const.DATE_FORMAT,
                    "hh:mm a"
                )
        editBinding.newTimeValueTV.text = dates + " (" + times + ")"
        editBinding.cancelTV.setOnClickListener {
            dialog.dismiss()
        }
        editBinding.editETV.setOnClickListener {
            dialog.dismiss()
            hitAPI()
        }

    }

    private fun hitAPI() {
        val params = Api3Params()
        params.put("ProviderRequest[booking_id]", data!!.id)
        params.put("ProviderRequest[availbility_id]", data!!.slotId!!.id)
        val call = api!!.hitEditRequest(params.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    private fun hitAcceptRejectAPI(id: Int, state: Int) {
        val call = api!!.apiChangeState(id, state, "")
        restFullClient!!.sendRequest(call, this)
    }

    private var dayList = ArrayList<SlotData>()

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
            } else if (responseUrl.contains(Const.API_AVAIL_GET_SLOT)) {
                if (responseCode == Const.STATUS_OK) {
                    val timeList = jsonobject.getJSONArray(Const.LIST)
                    dayList.clear()
                    when {
                        timeList.length() > 0 -> {
                            for (i in 0 until timeList.length()) {
                                val data = Gson().fromJson(
                                    timeList.getJSONObject(i).toString(),
                                    SlotData::class.java
                                )
                                data.day = localSlotDate
                                data.isFromServer = true
                                dayList.add(data)
                            }
                        }
                    }
                    if (timeList.length() > 0) {
                        setDialog()
                    } else {
                        showToastOne(getString(R.string.please_selct_anothere_date))
                    }

                }
            } else if (responseUrl.contains(Const.API_EDIT_REQUEST)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(getString(R.string.edit_sent_success))
                    hitOrderSummaryAPI()
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

    private var dialogBinding: AddTimeDialogBinding? = null

    private var dialog: AlertDialog? = null
    private fun setDialog() {
        dialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(baseActivity),
            R.layout.add_time_dialog,
            null,
            false
        )
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        mBuilder.setView(dialogBinding!!.root)
        dialog = mBuilder.create()
        dialog!!.show()
        dialogBinding!!.handleClick = this
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialogBinding!!.notificationRV.adapter =
            AdapterAvailableSlots(baseActivity!!, dayList, this, localSlotDate)

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
        if (data!!.latitude.isNotEmpty() && data!!.longitude.isNotEmpty()) {
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
                    ), 15f
                )
            )
        }
    }

}
