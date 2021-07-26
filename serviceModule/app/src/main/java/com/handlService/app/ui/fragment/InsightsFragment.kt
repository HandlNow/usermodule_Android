/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentInsightsBinding
import com.handlService.app.model.NotificationData
import com.handlService.app.model.ProfileData
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.adapter.AdapterReviews
import com.handlService.app.ui.adapter.BaseAdapter
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.handlService.app.utils.MyValueFormatter
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class InsightsFragment : BaseFragment(), ClickHandler, BaseAdapter.OnPageEndListener {

    private var binding: FragmentInsightsBinding? = null

    private var adapter: AdapterReviews? = null
    private var list: ArrayList<NotificationData> = arrayListOf()
    private var earningList: ArrayList<NotificationData> = arrayListOf()
    private var isSingle = false
    private var pageCount = 0
    private var type = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_insights, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        earningList.clear()
        type = Const.EARNING_TYPE_DAILY
        resetList()
        binding!!.handleClick = this
        baseActivity!!.setToolbar(getString(R.string.data_insights))
        setWhiteAll()
        binding!!.dailyTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.daycolor))
        binding!!.chartLL.visibility = View.VISIBLE

        setHasOptionsMenu(true)
        val arr = baseActivity!!.getProfileData()!!.totalParkings.split(" ")
        if (arr.isNotEmpty()) {
            when {
                arr[0].toFloat() > 0f -> {
                    binding!!.yearsTV.text = arr[0]
                    binding!!.textTV.text = baseActivity!!.getString(R.string.years_using_handl)
                }
                arr[2].toFloat() > 0f -> {
                    binding!!.yearsTV.text = arr[2]
                    binding!!.textTV.text = baseActivity!!.getString(R.string.months_using_handl)
                }
                arr[4].toFloat() > 0f -> {
                    binding!!.yearsTV.text = arr[4]
                    binding!!.textTV.text = baseActivity!!.getString(R.string.days_using_handl)
                }
            }
        }
        binding!!.taskTV.text = baseActivity!!.getProfileData()!!.completeBooking
        binding!!.selectDateTV.text = baseActivity!!.changeDateFormatFromDate(Calendar.getInstance().time, "dd-MM-yyyy")
        hitGetRatingAPI()
        hitCheckApi()
        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        binding!!.pullToRefresh.setOnRefreshListener {
            earningList.clear()
            resetList()
            hitGetRatingAPI()
            hitCheckApi()
            binding!!.pullToRefresh.isRefreshing = false
        }

    }

    private var date: DatePickerDialog.OnDateSetListener? = null
    private var myCalendar = Calendar.getInstance()
    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val date = sdf.format(myCalendar.time)
        binding!!.selectDateTV.text = date
        resetList()
        hitGetEarningsAPI()
    }

    private fun hitCheckApi() {
        val params = Api3Params()
        params.put("DeviceDetail[device_token]", baseActivity!!.getDeviceToken())
        params.put("DeviceDetail[device_type]", Const.ANDROID)
        params.put("DeviceDetail[device_name]", Build.MODEL)
        val call = api!!.apiCheck(params.getServerHashMap())
        restFullClient?.sendRequest(call, this)

    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = AdapterReviews(baseActivity!!, this, list)
            binding!!.reviewRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun hitGetRatingAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetRating(baseActivity!!.getProfileData()!!.id, pageCount)
            restFullClient!!.sendRequest(call, this)
        }

    }

    private fun hitGetEarningsAPI() {
        earningList.clear()
        val call = api!!.apiGetEarnings(type)
        restFullClient!!.sendRequest(call, this)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    private fun resetList() {
        list.clear()
        adapter = null
        isSingle = false
        pageCount = 0
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeMB) {
            (baseActivity as MainActivity).openDrawer()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setChartValues() {
        val labels = ArrayList<String>()

        val listData: ArrayList<BarEntry> = ArrayList()
        for (i in 0 until earningList.size) {
            labels.add("€")
            listData.add(BarEntry(i.toFloat(), earningList[i].amount.toFloat(), labels[i]))
        }

        binding!!.chart.animateY(5000)

        binding!!.chart.setTouchEnabled(true)
        binding!!.chart.isClickable = false
        binding!!.chart.isDoubleTapToZoomEnabled = false

        binding!!.chart.setDrawBorders(false)
        binding!!.chart.setDrawGridBackground(false)
        binding!!.chart.description.isEnabled = false
        binding!!.chart.legend.isEnabled = false

        binding!!.chart.axisLeft.setDrawGridLines(false)
        binding!!.chart.axisLeft.setDrawLabels(false)
        binding!!.chart.axisLeft.setDrawAxisLine(false)
        binding!!.chart.axisRight.setDrawGridLines(false)
        binding!!.chart.axisRight.setDrawLabels(false)
        binding!!.chart.axisRight.setDrawAxisLine(false)

        val xAxis = binding!!.chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f // only intervals of 1 day
        xAxis.setDrawLabels(true)
        xAxis.valueFormatter = IndexAxisValueFormatter(getAreaCount())


        val bardataset = BarDataSet(listData, "New DataSet")
        bardataset.color = ContextCompat.getColor(baseActivity!!, R.color.light_blue)//resolved color
        bardataset.valueTextColor = ContextCompat.getColor(baseActivity!!, R.color.light_blue)//resolved color
        bardataset.valueTextSize = 12f
        val data = BarData(bardataset)
        data.barWidth = 0.9f
        data.setValueFormatter(MyValueFormatter())
        binding!!.chart.data = data // set the data and list of labels into chart


    }

    private fun getAreaCount(): ArrayList<String> {
        val label: ArrayList<String> = ArrayList()
        for (i in 0 until earningList.size) {
            label.add(earningList[i].created)
        }
        return label
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.personalTV -> {
                binding!!.personalTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.daycolor))
                binding!!.generalTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
            }
            R.id.generalTV -> {
                binding!!.generalTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.daycolor))
                binding!!.personalTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
            }
            R.id.dailyTV -> {
                type = Const.EARNING_TYPE_DAILY
                setWhiteAll()
                binding!!.dailyTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.daycolor))
                binding!!.chartLL.visibility = View.VISIBLE
                hitGetEarningsAPI()
            }
            R.id.weeklyTV -> {
                type = Const.EARNING_TYPE_WEEKLY
                setWhiteAll()
                binding!!.weeklyTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.daycolor))
                binding!!.chartLL.visibility = View.VISIBLE
                hitGetEarningsAPI()


            }
            R.id.monthlyTV -> {
                type = Const.EARNING_TYPE_MONTHLY
                setWhiteAll()
                binding!!.monthlyTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.daycolor))
                binding!!.chartLL.visibility = View.VISIBLE

                hitGetEarningsAPI()

            }
            R.id.totalTV -> {
                type = Const.EARNING_TYPE_YEARLY
                setWhiteAll()
                binding!!.totalTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.daycolor))
                binding!!.totalLL.visibility = View.VISIBLE
                hitGetEarningsAPI()

            }

            R.id.selectDateTV -> {

                val dialog = DatePickerDialog(baseActivity!!, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH))
                dialog.datePicker.minDate = Calendar.getInstance().timeInMillis
                dialog.show()

            }


        }
    }

    private fun setWhiteAll() {
        binding!!.dailyTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.weeklyTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.monthlyTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.totalTV.setBackgroundColor(ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.totalLL.visibility = View.GONE
        binding!!.chartLL.visibility = View.GONE

    }

    override fun onPageEnd(vararg itemData: Any) {
        hitGetRatingAPI()
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_GET_RATING)) {
                if (responseCode == Const.STATUS_OK) {
                    pageCount++
                    val totalId = jsonobject.getJSONObject(Const._META).getInt(Const.PAGE_COUNT)
                    isSingle = pageCount >= totalId
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<NotificationData>(object1.toString(), NotificationData::class.java)
                        list.add(data)
                    }
                    if (list.size > 0) {
                        setAdapter()
                        binding!!.noDataTV.visibility = View.GONE
                    } else {
                        binding!!.noDataTV.visibility = View.VISIBLE

                    }

                } else {
                    isSingle = false
                }
            } else if (responseUrl.contains(Const.API_EARNING)) {
                if (responseCode == Const.STATUS_OK) {
                    val earning = jsonobject.optString("earning")
                    binding!!.totalEarnTV.text = baseActivity!!.getString(R.string.euro) + earning
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<NotificationData>(object1.toString(), NotificationData::class.java)
                        val date = baseActivity!!.changeDateFormatGmtToLocal(data.createdOn, Const.DATE_FORMAT, "dd-MMM-yyyy")
                        data.created = date
                        earningList.add(data)
                    }
                    setChartValues()
                }
            } else if (responseUrl.contains(Const.API_CHECK)) {
                val data = Gson().fromJson<ProfileData>(jsonobject.getJSONObject("detail").toString(), ProfileData::class.java)
                baseActivity!!.setProfileData(data)
                restFullClient!!.setLoginStatus(data.accessToken)
                baseActivity!!.updateDrawer()
                hitGetEarningsAPI()
            }

        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

}
