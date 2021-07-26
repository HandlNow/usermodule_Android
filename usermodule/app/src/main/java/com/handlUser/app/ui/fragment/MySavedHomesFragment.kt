/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentMySaveHomesBinding
import com.handlUser.app.model.AddressData
import com.handlUser.app.ui.adapter.AdapterSavedHomeList
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import org.json.JSONObject


class MySavedHomesFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentMySaveHomesBinding? = null
    private var adapter: AdapterSavedHomeList? = null
    private var userAddressList: ArrayList<AddressData> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_my_save_homes, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        resetList()
        baseActivity!!.setToolbar(title = getString(R.string.my_saved_homes))
        apiAddressList()

        binding!!.pullToRefresh.setOnRefreshListener {
            resetList()
            apiAddressList()
            binding!!.pullToRefresh.isRefreshing = false
        }

    }

    private fun resetList() {
        userAddressList.clear()
        adapter = null
    }

    private fun apiAddressList() {
        val call = api!!.apiAddressList()
        restFullClient!!.sendRequest(call, this)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.addRL, R.id.addTV -> {
                baseActivity!!.replaceFragment(EditRegisterHouseFragment())
            }

        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_ADD_ADDRESS)) {
                if (responseCode == Const.STATUS_OK) {
                    try {
                        val jsonArray = jsonObjects.getJSONArray("list")
                        for (i in 0 until jsonArray.length()) {
                            val object1 = jsonArray.getJSONObject(i)
                            val data = Gson().fromJson<AddressData>(object1.toString(), AddressData::class.java)
                            userAddressList.add(data)
                        }
                        setAdapter()
                    } catch (e: Exception) {
                        baseActivity!!.handleException(e)
                    }

                }

            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    private fun setAdapter() {
        if (userAddressList.size > 0) {
            adapter = AdapterSavedHomeList(baseActivity!!, userAddressList)
            binding!!.addressRV.adapter = adapter
        }
    }

}
