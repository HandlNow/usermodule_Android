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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentSubscriptionBinding
import com.handlService.app.model.PlanData
import com.handlService.app.ui.adapter.AdapterSubscription
import com.handlService.app.ui.adapter.BaseAdapter
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class SubscriptionFragment : BaseFragment(), ClickHandler, BaseAdapter.OnItemClickListener {

    private var binding: FragmentSubscriptionBinding? = null
    private var adapter: AdapterSubscription? = null
    private var arrayList: ArrayList<PlanData> = arrayListOf()
    private var pos = -1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_subscription, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(title = getString(R.string.edit_subscription))
        apiHitSub()
    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = AdapterSubscription(baseActivity!!, this, arrayList)
            binding!!.planRV.adapter = adapter
            adapter!!.setOnItemClickListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun apiHitSub() {
        val call = api!!.apiGetPlanList()
        restFullClient!!.sendRequest(call, this)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.continueTV -> {
                for (i in arrayList) {
                    if (i.isBuy) {
                        val data = baseActivity!!.getProfileData()
                        data!!.isSubscription = true
                        baseActivity!!.setProfileData(data)
                        break
                    }
                }
                baseActivity!!.onBackPressed()

            }
        }
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_PLAN_LIST)) {
                if (responseCode == Const.STATUS_OK) {

                    val jsonArray = jsonObject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val data = Gson().fromJson<PlanData>(jsonArray.getJSONObject(i).toString(), PlanData::class.java)
                        arrayList.add(data)
                    }
                    setAdapter()
                }
            } else if (responseUrl.contains(Const.API_PLAN_BUY)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(baseActivity!!.getString(R.string.plan_purchased_success))
                    binding!!.questionTV.text = baseActivity!!.getString(R.string.once_you_free_trial_is_over_which_subscription_system_do_you_want_be_transferred_to)
                    binding!!.continueTV.text = baseActivity!!.getString(R.string.save)
                    arrayList[pos].isBuy = true
                    adapter!!.notifyItemChanged(pos)
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }


    override fun onItemClick(vararg itemData: Any) {
        pos = itemData[0] as Int
        val data = arrayList[pos]

        AlertDialog.Builder(baseActivity!!,R.style.animateDialog)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.do_you_wantto_purchase_plan))
                .setPositiveButton(getString(R.string.yes)) { d, i ->
                    hitPurchaseAPI(data)

                    d.dismiss()
                }
                .setNegativeButton(getString(R.string.no)) { d, i ->
                    d.dismiss()
                }.show()


    }

    private fun hitPurchaseAPI(data: PlanData) {
        val param = Api3Params()
        param.put("Billing[subscription_id]", data.id)
        val call = api!!.apiBuyPlan(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)

    }
}
