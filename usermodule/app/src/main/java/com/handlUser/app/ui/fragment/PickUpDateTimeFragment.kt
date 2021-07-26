/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentPickupDatetimeBinding
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.utils.ClickHandler
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*


class PickUpDateTimeFragment : BaseFragment(), ClickHandler, TimePickerDialog.OnTimeSetListener {

    private var startTime: String = ""
    private var date: OnDateSetListener? = null
    private var binding: FragmentPickupDatetimeBinding? = null
    private var myCalendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_pickup_datetime, container, false
            )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(
            getString(R.string.pick_time_date),
            ContextCompat.getColor(baseActivity!!, R.color.White)
        )
        binding!!.timeDateCL.visibility = View.VISIBLE

        date = OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }
    }


    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val date = sdf.format(myCalendar.time)
        binding!!.selectDateTV.text = date
    }


    @SuppressLint("SetTextI18n")
    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.nextTV -> {
                when {
                    binding!!.selectDateTV.checkString().isEmpty() -> {
                        showToastOne(baseActivity!!.getString(R.string.please_select_date))
                    }
                    binding!!.selectTimeTV.checkString().isEmpty() -> {
                        showToastOne(baseActivity!!.getString(R.string.please_select_time))
                    }
                    else -> {
                        val bundle = requireArguments()
                        bundle.putString("date", binding!!.selectDateTV.checkString())
                        bundle.putString("time", binding!!.selectTimeTV.checkString())
                        baseActivity!!.replaceFragment(ResultFragment(), bundle)
                    }
                }
            }
            R.id.selectDateTV -> {

                val dialog = DatePickerDialog(
                    baseActivity!!, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)
                )
                dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
                dialog.show()

            }
            R.id.selectTimeTV -> {
                when {
                    binding!!.selectDateTV.checkString().isEmpty() -> {
                        showToastOne(baseActivity!!.getString(R.string.please_select_date))
                    }
                    else -> showTimePicker()
                }

            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun timeFormat(time: String): String {
        val sdf = SimpleDateFormat("H:mm")
        val startTime = sdf.parse(time)!!.time
        return SimpleDateFormat("h:mm a").format(startTime)
    }


    private var tpd: TimePickerDialog? = null

    private fun showTimePicker() {
        val now: Calendar = Calendar.getInstance()
        if (binding!!.selectDateTV.checkString() != baseActivity!!.changeDateFormatFromDate(
                Calendar.getInstance().time,
                "dd-MM-yyyy"
            )
        ) {
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
        val calendar: Calendar = Calendar.getInstance()
        if (binding!!.selectDateTV.checkString() == baseActivity!!.changeDateFormatFromDate(
                Calendar.getInstance().time,
                "dd-MM-yyyy"
            )
        ) {
            val min = calendar.get(Calendar.MINUTE)
            if (min == 0) {
                tpd!!.setMinTime(
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    Calendar.SECOND
                )

            } else {
                calendar.add(Calendar.HOUR_OF_DAY, 1)
                calendar.set(Calendar.SECOND, 0)
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
        val tpd =
            this.parentFragmentManager.findFragmentByTag("Timepickerdialog") as TimePickerDialog?
        if (tpd != null) tpd.onTimeSetListener = this
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        startTime = String.format("%02d:%02d", hourOfDay, minute) + ":00"
        val calendar: Calendar = Calendar.getInstance()
        val currentTime = String.format(
            "%02d:%02d",
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE)
        )

        if (binding!!.selectDateTV.checkString() == baseActivity!!.changeDateFormatFromDate(
                Calendar.getInstance().time,
                "dd-MM-yyyy"
            )
        ) {
            if (startTime >= currentTime) {
                binding!!.selectTimeTV.text = timeFormat(startTime)
            } else {
                startTime = ""
                showToastOne(baseActivity!!.getString(R.string.enter_startafter_currenttime))
            }
        } else {
            binding!!.selectTimeTV.text = timeFormat(startTime)
        }

        tpd = null

    }
}
