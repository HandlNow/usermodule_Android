
/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment.login

import android.os.Bundle
import android.os.SystemClock
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentLanguagesBinding
import com.handlService.app.model.NotificationData
import com.handlService.app.ui.adapter.AdapterLanguage
import com.handlService.app.ui.adapter.BaseAdapter
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class LanguagesFragment : BaseFragment(), ClickHandler, BaseAdapter.OnPageEndListener, BaseAdapter.OnItemClickListener {

    private var binding: FragmentLanguagesBinding? = null
    private var list: ArrayList<NotificationData> = arrayListOf()
    private var isSingle = false
    private var pageCount = 0
    private var mLastClickTime = SystemClock.elapsedRealtime()
    private var adapter: AdapterLanguage? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_languages, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.languages), screenBg = ContextCompat.getColor(baseActivity!!, R.color.White))
        binding!!.pullToRefresh.setOnRefreshListener {
            resetList()
            hitLanguageListAPI()
            binding!!.pullToRefresh.isRefreshing = false
        }
    }

    private fun resetList() {
        isSingle = false
        pageCount = 0
        list.clear()
        adapter = null
    }

    override fun onResume() {
        super.onResume()
        resetList()
        hitLanguageListAPI()
    }


    private fun hitLanguageListAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetAddedLanguageList(pageCount)
            restFullClient!!.sendRequest(call, this)
        }
    }


    private fun setAdapter() {
        if (adapter == null) {
            adapter = AdapterLanguage(baseActivity!!, list)
            binding!!.langRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
            adapter!!.setOnItemClickListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    override fun onHandleClick(view: View) {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
        when (view.id) {
            R.id.addTV -> {
                baseActivity!!.replaceFragment(ChooseLanguagesFragment())

            }
            R.id.doneTV -> {
                baseActivity!!.onBackPressed()
            }
        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_ADDED_LANGUAGE_LIST)) {
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
            } else if (responseUrl.contains(Const.API_DELETE_LANGUAGE)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(getString(R.string.lang_delete_success))
                    list.removeAt(pos)
                    adapter!!.notifyDataSetChanged()
                    if (list.size > 0) {
                        binding!!.noDataTV.visibility = View.GONE
                    } else {
                        binding!!.noDataTV.visibility = View.VISIBLE
                    }

                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    override fun onPageEnd(vararg itemData: Any) {
        hitLanguageListAPI()
    }

    var pos = 0
    override fun onItemClick(vararg itemData: Any) {
        pos = itemData[0] as Int
        val builder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        //set message for alert dialog
        builder.setMessage(baseActivity!!.getString(R.string.delete_language))
        builder.setPositiveButton(baseActivity!!.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss()
            hitDeleteAPI(list[pos].id)
        }
        builder.setNegativeButton(baseActivity!!.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun hitDeleteAPI(id: Int) {
        val call = api!!.apiLanguageDelete(id)
        restFullClient!!.sendRequest(call, this)
    }

}
