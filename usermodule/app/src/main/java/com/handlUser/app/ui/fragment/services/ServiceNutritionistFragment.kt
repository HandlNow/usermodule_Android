/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment.services

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.*
import com.handlUser.app.model.ServiceData
import com.handlUser.app.model.SubCategory
import com.handlUser.app.ui.adapter.AdapterDialogNumber
import com.handlUser.app.ui.adapter.BaseAdapter
import com.handlUser.app.ui.adapter.service.WallPaintingAdapter
import com.handlUser.app.ui.extensions.createArrayList
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.ui.fragment.PickUpDateTimeFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import org.json.JSONArray
import org.json.JSONObject


class ServiceNutritionistFragment : BaseFragment(), ClickHandler, BaseAdapter.OnItemClickListener, BaseAdapter.OnPageEndListener {

    private var binding: FragmentServiceNutritionistBinding? = null
    private var alertDialog: AlertDialog? = null
    private var data: SubCategory? = null
    private var list: ArrayList<ServiceData> = arrayListOf()
    private var isSingle = false
    private var pageCount = 0
    private var adapter: WallPaintingAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_service_nutritionist, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        resetList()
        binding!!.handleClick = this
        baseActivity!!.setToolbar(getString(R.string.nutritionist))
        hitGetDataAPI()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            data = arguments?.getParcelable<SubCategory>("data")
        }
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.nextTV -> {
                var isValid = false
                for (data in list[0].parent) {
                    if (data.value > 0) {
                        isValid = true
                        break
                    }
                }
                if (isValid) {
                    hitAddServiceAPI()
                } else {
                    showToastOne(baseActivity!!.getString(R.string.select_data_to_proceed))
                }
            }

        }
    }


    private fun resetList() {
        pageCount = 0
        isSingle = false
        list.clear()
        adapter = null
    }

    private fun hitGetDataAPI() {
        if (!isSingle) {
            isSingle = true
            val call = api!!.apiGetSubCategoryList(data!!.typeId, pageCount)
            restFullClient!!.sendRequest(call, this)
        }
    }

    private var dialogAdapter: AdapterDialogNumber? = null
    private var dialogBinding: DialogNumberBinding? = null

    private fun displayNumberDialog() {
        val list: ArrayList<String> = createArrayList()
        val mBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.dialog_number, null, false)
        mBuilder.setView(dialogBinding!!.root)
        dialogAdapter = AdapterDialogNumber(baseActivity!!, list)
        dialogBinding!!.notificationRV.adapter = dialogAdapter
        dialogAdapter!!.setOnItemClickListener(this)
        alertDialog = mBuilder.create()
        alertDialog!!.show()
        val displayRectangle = Rect()
        val window: Window = baseActivity!!.window

        window.decorView.getWindowVisibleDisplayFrame(displayRectangle)

        alertDialog!!.window!!.setLayout((displayRectangle.width() *
                0.9f).toInt(), (displayRectangle.height() * 0.6f).toInt())

        dialogBinding!!.closeIV.setOnClickListener {
            alertDialog!!.dismiss()
        }
    }

    var pos = 0
    override fun onItemClick(vararg itemData: Any) {
        when (itemData[0] as Int) {
            Const.ONE -> {
                pos = itemData[1] as Int
                displayNumberDialog()
            }
            Const.TWO -> {
                alertDialog!!.dismiss()
                val text = itemData[1] as String
                list[0].parent[pos].value = text.toInt()
                adapter?.notifyItemChanged(pos)
            }
        }

    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_PROVIDER_SUB_CATEGORY_LIST)) {
                if (responseCode == Const.STATUS_OK) {
                    pageCount++
                    val totalId = jsonobject.getJSONObject(Const._META).getInt(Const.PAGE_COUNT)
                    isSingle = pageCount >= totalId
                    val jsonArray = jsonobject.getJSONArray(Const.LIST)
                    for (i in 0 until jsonArray.length()) {
                        val object1 = jsonArray.getJSONObject(i)
                        val data = Gson().fromJson<ServiceData>(object1.toString(), ServiceData::class.java)
                        list.add(data)
                    }
                    if (list.size > 0) {
                        setAdapter()
                        binding!!.termCB.isChecked = list[0].rememberMe == Const.ONE
                    }

                } else {
                    isSingle = false
                }

            } else if (responseUrl.contains(Const.API_PROVIDER_ADD_SERVICE)) {
                if (responseCode == Const.STATUS_OK) {
                    val bundle = arguments
                    bundle?.putBoolean("is_check", binding!!.termCB.isChecked)
                    bundle?.putInt("service_id", jsonobject.getJSONObject("detail").optInt("id"))
                    baseActivity!!.replaceFragment(ServiceExtraRequireFragment(), bundle!!)
                }

            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    private fun setAdapter() {
        if (adapter == null) {
            adapter = WallPaintingAdapter(baseActivity!!, list[0].parent)
            binding!!.commonRV.adapter = adapter
            adapter!!.setOnPageEndListener(this)
            adapter!!.setOnItemClickListener(this)
        } else {
            adapter!!.notifyDataSetChanged()
        }

    }

    override fun onPageEnd(vararg itemData: Any) {
        hitGetDataAPI()
    }

    private fun hitAddServiceAPI() {
        val param = Api3Params()

        param.put("UserService[category_id]", data!!.categoryId)
        param.put("UserService[sub_category_id]", data!!.id)
        if (binding!!.termCB.isChecked) {
            param.put("UserService[remember_me]", Const.ONE)
        } else {
            param.put("UserService[remember_me]", Const.ZERO)
        }
        param.put("source", getJsonArray())

        val call = api!!.apiAddService(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)

    }

    private fun getJsonArray(): JSONArray {
        val array = JSONArray()
        for (data in list[0].parent) {
            if (data.value > 0) {
                val jsonObject = JSONObject()
                jsonObject.put("parent_id", data.id)
                jsonObject.put("is_parent_check", "")
                jsonObject.put("is_parent_quantity", data.value)
                jsonObject.put("kid_id", "")
                jsonObject.put("children_id", "")
                jsonObject.put("is_children_check", "")
                jsonObject.put("is_children_quantity", "")
                array.put(jsonObject)
            }
        }
        return array
    }
}
