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
import android.text.method.ScrollingMovementMethod
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentSummaryBinding
import com.handlUser.app.model.LangData
import com.handlUser.app.model.NotificationData
import com.handlUser.app.model.ProviderData
import com.handlUser.app.ui.activity.MainActivity
import com.handlUser.app.ui.adapter.AdapterReviews
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.ImageAdapter
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.makeScrollableInsideScrollView
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.login.NotificationFragment
import com.handlUser.app.utils.Const
import org.json.JSONObject
import java.util.*


class SummaryFragment : BaseFragment(), BaseAdapter.OnPageEndListener {

    private var binding: FragmentSummaryBinding? = null
    private var providerData: ProviderData? = null
    private var adapter: AdapterReviews? = null
    private var list: ArrayList<NotificationData> = arrayListOf()
    private var isSingle = false
    private var pageCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            providerData = arguments?.getParcelable<ProviderData>("provider_data")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_summary, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        resetList()
        initUI()
    }

    private fun resetList() {
        list.clear()
        adapter = null
        isSingle = false
        pageCount = 0

    }

    @SuppressLint("SimpleDateFormat")
    private fun initUI() {
        resetList()
        setHasOptionsMenu(true)
        baseActivity!!.setToolbar(title = getString(R.string.summary), ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.providerData = providerData

        binding!!.userIV.loadFromUrl(baseActivity!!, providerData!!.profileFile)
        if (providerData!!.isTranspotation == Const.TYPE_ON) {
            baseActivity!!.setTextViewDrawableColor(binding!!.dateTV, R.color.dark_blue)
        } else {
            baseActivity!!.setTextViewDrawableColor(binding!!.dateTV, R.color.carColor)
        }
        val arr = providerData!!.totalParkings.split(" ")
        binding!!.countTwoTV.text = arr[0]
        binding!!.countOneTV.text = providerData!!.completeBooking
        hitGetRatingAPI()
        binding!!.ratingTV.text = providerData!!.rating.toString()
        binding!!.nameTV.makeScrollableInsideScrollView()
        binding!!.nameTV.text = getMultiId(providerData!!.selecteLanguage)
        binding!!.photosRV.adapter = ImageAdapter(baseActivity!!, providerData!!.providerService[0].providerFile)
        if (providerData!!.providerService[0].providerFile.isNotEmpty()) {
            binding!!.skilldescTV.text = providerData!!.providerService[0].providerFile[0].description
        }

    }



    private fun hitGetRatingAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetRating(providerData!!.id, pageCount)
            restFullClient!!.sendRequest(call, this)
        }
    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = AdapterReviews(baseActivity!!, this, list, providerData!!.fullName)
            binding!!.reviewRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_noti, menu)
        val item = menu.findItem(R.id.notiIV)
        val countTV = item.actionView.findViewById<AppCompatTextView>(R.id.countTV)
        countTV.text = baseActivity!!.getProfileData()?.notificationCount.toString()
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
            }

        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    override fun onPageEnd(vararg itemData: Any) {
        hitGetRatingAPI()
    }

    private fun getMultiId(colorList: ArrayList<LangData>): String {
        var id = ""
        for (data in colorList) {
            id = id +data.languageTitle + ","
        }
        return when {
            id.isNotEmpty() -> id.substring(0, id.length - 1)
            else -> id
        }
    }
}
