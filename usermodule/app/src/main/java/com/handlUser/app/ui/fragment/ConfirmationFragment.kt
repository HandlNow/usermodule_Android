/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentConfirmationBinding
import com.handlUser.app.utils.ClickHandler
import java.util.*
import kotlin.collections.ArrayList


class ConfirmationFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentConfirmationBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_confirmation, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.confirmation))

        setDefaultClick()
        binding!!.dayViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.LightGrey))
        binding!!.weekView.visibility = View.VISIBLE
        binding!!.weekView.numberOfVisibleDays = 1

        binding!!.calendarView.setOnDayClickListener(object : OnDayClickListener {
            override fun onDayClick(eventDay: EventDay) {
                val clickedDayCalendar = eventDay.calendar

            }
        })

    }




    private fun setDefaultClick() {
        binding!!.dayViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.weekViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.monthViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.weekView.visibility = View.GONE
        binding!!.calendarView.visibility = View.GONE

    }

    private fun setDots() {
        val events: MutableList<EventDay> = ArrayList()
        val calendar: Calendar = Calendar.getInstance()
        events.add(EventDay(calendar, R.drawable.circle_light_blue))
        //or if you want to specify event label color
        events.add(EventDay(calendar, R.drawable.circle_gray, Color.parseColor("#228B22")))
        binding!!.calendarView.setEvents(events)

    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.dayViewTV -> {
                setDefaultClick()
                binding!!.dayViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.LightGrey))
                binding!!.weekView.visibility = View.VISIBLE
                binding!!.weekView.numberOfVisibleDays = 1

            }
            R.id.weekViewTV -> {
                setDefaultClick()
                binding!!.weekViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.LightGrey))
                binding!!.weekView.visibility = View.VISIBLE
                binding!!.weekView.numberOfVisibleDays = 7

            }

            R.id.monthViewTV -> {
                setDefaultClick()
                binding!!.monthViewTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.LightGrey))
                binding!!.calendarView.visibility = View.VISIBLE
                setDots()

            }

        }
    }



}