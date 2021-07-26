/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.AddScheduleDialogBinding
import com.handlService.app.databinding.DialogDeleteSlotBinding
import com.handlService.app.databinding.FragmentAvailabilitySlotsBinding
import com.handlService.app.model.ProfileData
import com.handlService.app.model.SlotData
import com.handlService.app.ui.adapter.AdapterAvailableSlots
import com.handlService.app.ui.extensions.checkString
import com.handlService.app.ui.extensions.isBlank
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class AvailabilitySlotsFragment : BaseFragment(), ClickHandler, TimePickerDialog.OnTimeSetListener {

    private var day: String? = null
    private var dayName: String? = null
    private var dayofWeek: Int? = null
    private var binding: FragmentAvailabilitySlotsBinding? = null
    private var dialogBinding: AddScheduleDialogBinding? = null
    private var dialogDeleteBinding: DialogDeleteSlotBinding? = null
    private var dialog: Dialog? = null
    private var timeType: Boolean = false
    private var deletePosition: Int = 0
    private var startTime: String = ""
    private var endTime: String = ""
    private var dayList = ArrayList<SlotData>()
    private var adapter: AdapterAvailableSlots? = null
    private var tpd: TimePickerDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_availability_slots, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.availability))
        binding!!.calendarView.selectedDate = CalendarDay.today()
        hitCheckApi()
        binding!!.calendarView.state().edit()
                .setMinimumDate(CalendarDay.today())
                .commit()
        binding!!.calendarView.setOnDateChangedListener { widget, date, selected ->
            var month=date.month.toString()
            if (month.toInt() < 9) {
                month = "0" + (date.month + 1).toString()
            } else month = (date.month + 1).toString()
            day = date.year.toString() + "-" + month+ "-" + date.day.toString()

            dayofWeek = baseActivity!!.dayOfWeek(day!!)
            dayName = baseActivity!!.changeDateFormat(day, "yyyy-MM-dd", "EEEE")
            hitGetSlotAPI()
        }

        if (day == null) {
            day = CalendarDay.today().year.toString() + "-" + (CalendarDay.today().month + 1).toString() + "-" + CalendarDay.today().day.toString()
            dayofWeek = baseActivity!!.dayOfWeek(day!!)
            dayName = baseActivity!!.changeDateFormat(day, "yyyy-MM-dd", "EEEE")
        }
        hitGetSlotAPI()
        if (baseActivity!!.getProfileData()!!.isAvailable) {
            binding!!.doneTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.mid_blue_button)
        } else {
            binding!!.doneTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.lightgrey_button)
        }
    }


    private fun hitGetSlotAPI() {
        val params = Api3Params()
        params.put("Availability[start_time]",baseActivity!!.changeDateFormatToGmt(day+ " " + Const.DATE_00_00_00,Const.DATE_FORMAT,Const.DATE_FORMAT))
        params.put("Availability[end_time]",baseActivity!!.changeDateFormatToGmt(day+ " " + Const.DATE_23_59_59,Const.DATE_FORMAT,Const.DATE_FORMAT))
        //params.put("Availability[day_id]", dayofWeek!!)
        val call = api!!.hitGetSlotAvailabilities(params.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    private fun hitAddSlotAPI() {
        val params = Api3Params()
        when {
            dialogBinding!!.checkCB.isChecked -> {
                params.put("Availability[recurring_id]", Const.IS_RECURRING)
            }
            dialogBinding!!.everyDayCB.isChecked -> {
                if (dayofWeek == 0 || dayofWeek == 6) {
                    params.put("Availability[recurring_id]", Const.WEEKEND_RECURRING)
                } else {
                    params.put("Availability[recurring_id]", Const.WEEKDAYS_RECURRING)
                }
            }
            else -> {
                params.put("Availability[recurring_id]", Const.NOT_RECURRING)
            }
        }
        params.put("Availability[day_id]", dayofWeek!!)
        params.put("Availability[created_by_id]", baseActivity!!.getProfileData()?.id!!)
        params.put("Availability[start_time]", baseActivity!!.changeDateFormatToGmt(day + " " + startTime, Const.DATE_FORMAT, Const.DATE_FORMAT))
        params.put("Availability[end_time]", baseActivity!!.changeDateFormatToGmt(day + " " + endTime, Const.DATE_FORMAT, Const.DATE_FORMAT))
        val call = api!!.hitAddSlotAvailabilities(params.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    private fun hitDeleteSlotAPI(slotId: Int, type: Int) {
        val call = api!!.hitDeleteSlotAvailabilities(slotId, type)
        restFullClient!!.sendRequest(call, this)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.saveTV -> {
                openTimeDialog()
            }
            R.id.saveTimeBT -> {
                if (checkTime()) {
                    hitAddSlotAPI()
                }
            }
            R.id.crossIV -> {
                dialog!!.dismiss()
            }
            R.id.doneTV -> {
                if (baseActivity!!.getProfileData()!!.isAvailable) {
                    baseActivity!!.onBackPressed()
                }
            }
            R.id.cancelTV -> {
                dialog?.dismiss()
            }
            R.id.allDeleteTV -> {
                dialog?.dismiss()
                if (dayList[deletePosition].isFromServer) {
                    hitDeleteSlotAPI(dayList[deletePosition].id, Const.EVERY_DAY)
                } else {
                    deleteItem(deletePosition)
                }
            }
            R.id.singleDeleteTV -> {
                dialog?.dismiss()
                if (dayList[deletePosition].isFromServer) {
                    hitDeleteSlotAPI(dayList[deletePosition].id, Const.ONE_DAY)
                } else {
                    deleteItem(deletePosition)
                }
            }

        }
    }

    private fun checkTime(): Boolean {
        when {
            dialogBinding!!.startTimeET.isBlank() -> {
                showToastOne(getString(R.string.start_time_valid))
            }
            dialogBinding!!.endTimeET.isBlank() -> {
                showToastOne(getString(R.string.end_time_valid))
            }
            startTime > endTime -> {
                showToastOne(getString(R.string.end_time_more_valid))
            }
            startTime == endTime -> {
                showToastOne(getString(R.string.end_timevalid))
            }
            else -> {
                return true
            }
        }
        return false
    }

    @SuppressLint("SetTextI18n")
    private fun openTimeDialog() {
        dialogBinding = AddScheduleDialogBinding.inflate(LayoutInflater.from(context))
        dialogBinding!!.handleClick = this
        dialog = Dialog(baseActivity!!)
        dialog!!.setContentView(dialogBinding!!.root)
        dialog!!.show()
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        dialogBinding!!.checkCB.text = baseActivity!!.getString(R.string.apply_to_every_monday) + dayName
        if (dayName == baseActivity!!.getString(R.string.saturdy) || dayName == baseActivity!!.getString(R.string.sunday)) {
            dialogBinding!!.everyDayCB.text = baseActivity!!.getString(R.string.apply_to_all_weekends)
        } else {
            dialogBinding!!.everyDayCB.text = baseActivity!!.getString(R.string.apply_to_all_weekdays)
        }
        dialogBinding!!.startTimeET.setOnClickListener {
            timeType = true
            dialogBinding!!.endTimeET.setText("")
            showTimePicker()
        }
        dialogBinding!!.endTimeET.setOnClickListener {

            if (dialogBinding!!.startTimeET.checkString().isEmpty()) {
                showToast(baseActivity!!.getString(R.string.select_start_time))
            } else {
                timeType = false
                showTimePicker()
            }
        }
        dialogBinding!!.checkCB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dialogBinding!!.everyDayCB.isChecked = false
            }
        }
        dialogBinding!!.everyDayCB.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                dialogBinding!!.checkCB.isChecked = false
            }
        }
    }


    private fun showTimePicker() {
        val now: Calendar = Calendar.getInstance()
        if (binding!!.calendarView.selectedDate != CalendarDay.today()) {
            now.set(Calendar.HOUR_OF_DAY, 0)
            now.set(Calendar.MINUTE, 0)
            now.set(Calendar.SECOND, 0)
        }
        if (tpd == null) {
            tpd = TimePickerDialog.newInstance(
                    this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
            )
        } else {
            tpd!!.initialize(
                    this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    now.get(Calendar.SECOND),
                    false
            )
        }
        tpd!!.isThemeDark = false
        tpd!!.dismissOnPause(true)
        when {
            timeType -> {

                tpd!!.setTimeInterval(1, 30, 60)

                val calendar: Calendar = Calendar.getInstance()
                if (binding!!.calendarView.selectedDate == CalendarDay.today()) {
                    val min = calendar.get(Calendar.MINUTE)
                    if (min == 0) {
                        tpd!!.setMinTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), Calendar.SECOND)

                    } else {
                        calendar.add(Calendar.HOUR_OF_DAY, 1)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        tpd!!.setMinTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), Calendar.SECOND)
                    }
                }


            }
            else -> {

                val startTime = startTime.split(":")
                if (startTime.isNotEmpty()) {
                    tpd!!.setMinTime(startTime[0].toInt(), startTime[1].toInt(), 60)
                }
                tpd!!.setTimeInterval(1, 30, 60)
            }
        }
        tpd!!.setOnCancelListener { dialogInterface ->
            tpd = null
        }
        tpd!!.show(this.parentFragmentManager, "Timepickerdialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        tpd = null
    }

    override fun onResume() {
        super.onResume()
        val tpd = this.parentFragmentManager.findFragmentByTag("Timepickerdialog") as TimePickerDialog?
        if (tpd != null) tpd.onTimeSetListener = this
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {

        if (minute % 30 == 0) {
            when {
                timeType -> {

                    startTime = String.format("%02d:%02d", hourOfDay, minute) + ":00"
                    val calendar: Calendar = Calendar.getInstance()
                    val currentTime = String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))

                    if (binding!!.calendarView.selectedDate == CalendarDay.today()) {
                        if (startTime >= currentTime) {
                            dialogBinding!!.startTimeET.setText(timeFormat(startTime))
                        } else {
                            startTime = ""
                            showToastOne(baseActivity!!.getString(R.string.enter_startafter_currenttime))
                        }
                    } else {
                        dialogBinding!!.startTimeET.setText(timeFormat(startTime))
                    }
                }
                else -> {

                    endTime = String.format("%02d:%02d", hourOfDay, minute) + ":00"
                    dialogBinding!!.endTimeET.setText(timeFormat(endTime))
                }
            }
        } else {
            showToastOne(baseActivity!!.getString(R.string.please_select_minute))
        }
        tpd = null

    }

    @SuppressLint("SimpleDateFormat")
    private fun timeFormat(time: String): String {
        val sdf = SimpleDateFormat("H:mm")
        val startTime = sdf.parse(time)?.time
        return SimpleDateFormat("h:mm a").format(startTime)
    }


    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObject = JSONObject(response!!)

            if (responseUrl.contains(Const.API_AVAIL_ADD_SLOT)) {
                if (responseCode == Const.STATUS_OK) {
                    if (jsonObject.has("message")) {
                        showToast(jsonObject.optString("message"))
                    }
                    dialog!!.dismiss()
                    dayList.clear()
                    adapter = null
                    hitGetSlotAPI()
                    hitCheckApi()
                }
            } else if (responseUrl.contains(Const.API_AVAIL_DELETE_SLOT)) {
                if (responseCode == Const.STATUS_OK) {
                    if (jsonObject.has("message")) {
                        showToast(jsonObject.optString("message"))
                    }
                    dayList.removeAt(deletePosition)
                    setAdapter()
                    if (dayList.size > 0) {
                        binding!!.noDataIV.visibility = View.GONE
                    } else {
                        binding!!.noDataIV.visibility = View.VISIBLE
                    }
                    hitCheckApi()

                }
            } else if (responseUrl.contains(Const.API_AVAIL_GET_SLOT)) {
                if (responseCode == Const.STATUS_OK) {
                    val timeList = jsonObject.getJSONArray(Const.LIST)
                    dayList.clear()
                    when {
                        timeList.length() > 0 -> {
                            for (i in 0 until timeList.length()) {
                                val data = Gson().fromJson(timeList.getJSONObject(i).toString(), SlotData::class.java)
                                data.day = day
                                data.isFromServer = true
                                dayList.add(data)
                            }
                        }
                    }
                    setAdapter()
                    if (dayList.size > 0) {
                        binding!!.noDataIV.visibility = View.GONE
                    } else {
                        binding!!.noDataIV.visibility = View.VISIBLE

                    }

                }
            } else if (responseUrl.contains(Const.API_CHECK)) {
                val data = Gson().fromJson<ProfileData>(jsonObject.getJSONObject("detail").toString(), ProfileData::class.java)
                baseActivity!!.setProfileData(data)
                restFullClient!!.setLoginStatus(data.accessToken)
                baseActivity!!.updateDrawer()
                if (baseActivity!!.getProfileData()!!.isAvailable) {
                    binding!!.doneTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.mid_blue_button)
                } else {
                    binding!!.doneTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.lightgrey_button)
                }
            }
        } catch (e: JSONException) {
            handleException(e)
        }
    }

    private fun setAdapter() {
        when (adapter) {
            null -> binding!!.timeRV.adapter = AdapterAvailableSlots(baseActivity!!, dayList, this, day)
            else -> adapter!!.notifyDataSetChanged()
        }

    }

    @SuppressLint("SetTextI18n")
    fun onDelete(adapterPosition: Int) {
        deletePosition = adapterPosition
        dialogDeleteBinding = DialogDeleteSlotBinding.inflate(LayoutInflater.from(context))
        dialogDeleteBinding!!.handleClick = this
        dialog = Dialog(baseActivity!!)
        dialog!!.setContentView(dialogDeleteBinding!!.root)
        dialog!!.show()
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)

        val dayName = baseActivity!!.changeDateFormatGmtToLocal(dayList[deletePosition].startTime, Const.DATE_FORMAT, "EEEE")
        dialogDeleteBinding!!.singleDeleteTV.text = baseActivity!!.getString(R.string.delete_only_for_this_monday) + dayName
        dialogDeleteBinding!!.allDeleteTV.text = baseActivity!!.getString(R.string.delete_for_every_monday) + dayName
    }

    private fun deleteItem(deletePosition: Int) {
        dayList.removeAt(deletePosition)
        adapter?.notifyDataSetChanged()
    }
    private fun hitCheckApi() {
        val params = Api3Params()
        params.put("DeviceDetail[device_token]", baseActivity!!.getDeviceToken())
        params.put("DeviceDetail[device_type]", Const.ANDROID)
        params.put("DeviceDetail[device_name]", Build.MODEL)
        val call = api!!.apiCheck(params.getServerHashMap())
        restFullClient?.sendRequest(call, this)

    }

}