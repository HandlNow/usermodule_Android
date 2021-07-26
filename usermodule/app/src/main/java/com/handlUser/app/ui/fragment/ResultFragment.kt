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
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentResultBinding
import com.handlUser.app.model.ProviderData
import com.handlUser.app.model.ProviderService
import com.handlUser.app.model.SubCategory
import com.handlUser.app.ui.activity.MainActivity
import com.handlUser.app.ui.adapter.AdapterResults
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.login.NotificationFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class ResultFragment : BaseFragment(), ClickHandler, BaseAdapter.OnItemClickListener {

    private var binding: FragmentResultBinding? = null
    private var list: ArrayList<ProviderData> = arrayListOf()
    private var reqList: ArrayList<ProviderData> = arrayListOf()
    private var reqOtherList: ArrayList<ProviderData> = arrayListOf()
    private var adapter: AdapterResults? = null
    private var reqAdapter: AdapterResults? = null
    private var isSingle = false
    private var pageCount = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_result, container, false
            )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolbar(
            title = getString(R.string.results),
            ContextCompat.getColor(baseActivity!!, R.color.White)
        )
        setHasOptionsMenu(true)
        binding!!.handleClick = this
        binding!!.pullToRefresh.setOnRefreshListener {
            displayDateTime()
            resetList()
            hitCategoryListAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }

    }

    override fun onResume() {
        super.onResume()
        displayDateTime()
        resetList()
        hitCategoryListAPI()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun displayDateTime() {
        binding!!.timeTV.text = arguments?.getString("time", "")
        val date = arguments?.getString("date", "")
        binding!!.monthTV.text = baseActivity!!.changeDateFormat(date, "dd-MM-yyyy", "MMMM")
        binding!!.dayTV.text = baseActivity!!.changeDateFormat(date, "dd-MM-yyyy", "dd (EEE)")

        val simpleDateFormat = SimpleDateFormat("hh:mm a")

        val date1 = simpleDateFormat.parse(arguments?.getString("time", "")!!)
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
        binding!!.timeAvailTV.text =
            baseActivity!!.getString(R.string.availabile_in) + hour.toString() + baseActivity!!.getString(
                R.string.hourss
            )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_summary, menu)
        val item = menu.findItem(R.id.notiIV)
        val countTV = item.actionView.findViewById<AppCompatTextView>(R.id.countTV)
        countTV.text = baseActivity!!.getProfileData()?.notificationCount.toString()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.filterIV -> {
                baseActivity!!.replaceFragment(FilterFragment())

            }
            R.id.notiIV -> {
                baseActivity!!.replaceFragment(NotificationFragment())
            }
            R.id.menuIV -> {
                (baseActivity as MainActivity).openDrawer()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun resetList() {
        list.clear()
        adapter = null
        reqList.clear()
        adapter = null
        reqOtherList.clear()
        adapter = null
        pageCount = 0
        isSingle = false
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
            if (responseUrl.contains(Const.API_PROVIDER_RESULT_LIST)) {
                val datas = arguments?.getParcelable<SubCategory>("data")
                if (responseCode == Const.STATUS_OK) {
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    pageCount++
                    val totalId = jsonobject.getJSONObject(Const._META).getInt(Const.PAGE_COUNT)
                    isSingle = pageCount >= totalId
                    for (k in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(k)
                        val data = Gson().fromJson<ProviderData>(
                            object1.toString(),
                            ProviderData::class.java
                        )
                        var service: ProviderService? = null
                        for (x in data.providerService) {
                            if (datas!!.categoryId == x.categoryId && datas.id == x.subCategoryId) {
                                service = x
                                break
                            }
                        }
                        if (service != null) {
                            data.providerService.clear()
                            data.providerService.add(service!!)
                        }

                        if (data.isAvailableProvider) {
                            list.add(data)
                        }
                        if (data.avalibility.isNotEmpty()) {
                            for (slot in 0 until data.avalibility.size) {
                                data.avalibility[slot].visibility =
                                    data.avalibility[slot].availabilitySlots.isNotEmpty()
                            }
                        }

                    }
                    if (list.size > 0) {
                        setAdapter()
                        binding!!.noDataTV.visibility = View.GONE
                        binding!!.resultRV.visibility = View.VISIBLE
                    } else {
                        binding!!.noDataTV.visibility = View.VISIBLE
                        binding!!.resultRV.visibility = View.GONE
                    }
                }
            } else if (responseUrl.contains(Const.API_PROVIDER_RESULT_DATE_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (k in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(k)
                        val data = Gson().fromJson<ProviderData>(
                            object1.toString(),
                            ProviderData::class.java
                        )
                        if (data.avalibility.isNotEmpty()) {
                            for (slot in 0 until data.avalibility.size) {
                                data.avalibility[slot].visibility =
                                    data.avalibility[slot].availabilitySlots.isNotEmpty()
                            }
                        }
                        reqList.add(data)
                    }
                    if (reqList.size > 0) {
                        setReqAdapter()
                        binding!!.noReqTV.visibility = View.GONE
                        binding!!.reqRV.visibility = View.VISIBLE
                    } else {
                        binding!!.noReqTV.visibility = View.VISIBLE
                        binding!!.reqRV.visibility = View.GONE
                    }
                }
            } else if (responseUrl.contains(Const.API_PROVIDER_RESULT_DATE_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (k in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(k)
                        val data = Gson().fromJson<ProviderData>(
                            object1.toString(),
                            ProviderData::class.java
                        )
                        if (data.avalibility.isNotEmpty()) {
                            for (slot in 0 until data.avalibility.size) {
                                data.avalibility[slot].visibility =
                                    data.avalibility[slot].availabilitySlots.isNotEmpty()
                            }
                        }
                        reqOtherList.add(data)
                    }
                    if (reqOtherList.size > 0) {
                        setReqAdapter()
                        binding!!.noReqotherTV.visibility = View.GONE
                        binding!!.reqotherRV.visibility = View.VISIBLE
                    } else {
                        binding!!.noReqotherTV.visibility = View.VISIBLE
                        binding!!.reqotherRV.visibility = View.GONE
                    }
                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = AdapterResults(baseActivity!!, list)
            binding!!.resultRV.adapter = adapter
            adapter!!.setOnItemClickListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun setReqAdapter() {
        if (reqAdapter == null) {
            reqAdapter = AdapterResults(baseActivity!!, reqList)
            binding!!.reqRV.adapter = reqAdapter
            reqAdapter!!.setOnItemClickListener(this)
        } else {
            reqAdapter!!.notifyDataSetChanged()
        }
    }

    private fun hitCategoryListAPI() {
        val date = arguments?.getString("date", "")
        val time = arguments?.getString("time", "")
        val data = arguments?.getParcelable<SubCategory>("data")
        val date_time = baseActivity!!.changeDateFormatToGmt(
            date + " " + time,
            "dd-MM-yyyy h:mm a",
            Const.DATE_FORMAT
        )
        if (!isSingle) {
            isSingle = true
            val params = Api3Params()
            params.put("date", date_time)
            params.put("ProviderCategory[category_id]", data!!.categoryId)
            params.put("ProviderCategory[sub_category_id]", data.id)
            params.put("latitude", baseActivity!!.store!!.getString(Const.HOME_LAT, "") as String)
            params.put("longitude", baseActivity!!.store!!.getString(Const.HOME_LONG, "") as String)
            params.put("filter", baseActivity!!.store?.getInt("filter") as Int)
            val call = api!!.apiGetResultList(
                params.getServerHashMap(),
                arguments?.getInt("requirements", 0) as Int,
                pageCount
            )
            restFullClient!!.sendRequest(call, this)
        }
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.timeTV, R.id.monthTV, R.id.dayTV -> {
                baseActivity!!.replaceFragment(PickUpDateTimeFragment(), requireArguments())
            }
        }
    }

    override fun onItemClick(vararg itemData: Any) {
        val pos = itemData[0] as Int
        val bundle = arguments
        bundle!!.putParcelable("provider_data", list[pos])
        baseActivity!!.replaceFragment(RequestServiceFragment(), bundle)
    }


}
