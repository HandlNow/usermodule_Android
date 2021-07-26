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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlService.app.R
import com.handlService.app.databinding.FragmentServicePricesBinding
import com.handlService.app.model.MyServiceData
import com.handlService.app.model.SubCategory
import com.handlService.app.ui.extensions.afterTextChanged
import com.handlService.app.ui.extensions.checkString
import com.handlService.app.ui.extensions.replaceFragWithArgs
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.utils.ClickHandler
import com.handlService.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import org.json.JSONObject


class ServicePricesFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentServicePricesBinding? = null
    private var subData: SubCategory? = null
    private var serviceData: MyServiceData? = null
    private var priceId = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_service_prices, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            when {
                arguments?.containsKey("list_data") == true -> {
                    subData = arguments?.getParcelable<SubCategory>("list_data")
                }
                else -> {
                    serviceData = arguments?.getParcelable<MyServiceData>("service_data")
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        binding!!.handleClick = this
        when {
            subData != null -> {
                setTitle(subData!!.title)
                if (subData!!.isCheck) {
                    hitGetPriceAPI()
                }
            }
            else -> {
                    setTitle(serviceData!!.subCategoryName)
                    priceId = serviceData!!.id
                    if (!serviceData!!.price.isNullOrEmpty()) {
                        binding!!.priceTV.text = baseActivity!!.getString(R.string.euro) + serviceData!!.price.toFloat().toString() + "/h"
                        binding!!.priceET.setText(serviceData!!.price.toFloat().toString())
                    }

            }
        }

        binding!!.priceET.afterTextChanged {
            binding!!.priceTV.text = baseActivity!!.getString(R.string.euro) + binding!!.priceET.checkString() + "/h"

        }
    }

    private fun hitGetPriceAPI() {
        val param = Api3Params()
        param.put("ProviderCategory[category_id]", subData!!.categoryId)
        param.put("ProviderCategory[sub_category_id]", subData!!.id)
        val call = api!!.apiGetCategoryPrice(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }


    private fun setTitle(title: String) {
        baseActivity!!.setToolbar("$title: Charging rate")
    }

    private fun hitAddPriceApi() {
        val param = Api3Params()
        param.put("ProviderCategory[category_id]", subData!!.categoryId)
        param.put("ProviderCategory[sub_category_id]", subData!!.id)
        param.put("ProviderCategory[price]", binding!!.priceET.checkString())
        param.put("ProviderCategory[description]", arguments?.getString("desc") ?: "")

        if (arguments?.getBoolean("isMyService")!!) {
            param.put("provider_subcategory", arguments?.getString("source") as String)
        }
        val call = api!!.apiAddCategory(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    private fun hitUpdatePriceApi() {
        val param = Api3Params()
        param.put("ProviderCategory[id]", priceId)
        param.put("ProviderCategory[price]", binding!!.priceET.checkString())
        if (arguments?.getBoolean("isMyService")!!) {
            param.put("provider_subcategory", arguments?.getString("source") as String)
        }
        val call = api!!.apiUpdateCategoryPrice(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    @SuppressLint("SetTextI18n")
    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_CATEGORY_ADD_PRICE)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(jsonObjects.optString(Const.MESSAGE)
                            ?: resources.getString(R.string.service_add_successfully))
                    val data = Gson().fromJson<MyServiceData>(jsonObjects.getJSONObject("detail").toString(), MyServiceData::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable("data", data)
                    baseActivity!!.replaceFragment(RequirementFragment(), args = bundle)
                }
            } else if (responseUrl.contains(Const.API_CATEGORY_UPDATE_PRICE)) {
                if (responseCode == Const.STATUS_OK) {
                    showToastOne(resources.getString(R.string.service_update_successfully))
                    val data = Gson().fromJson<MyServiceData>(jsonObjects.getJSONObject("detail").toString(), MyServiceData::class.java)
                    val bundle = Bundle()
                    bundle.putParcelable("data", data)
                    baseActivity!!.replaceFragment(RequirementFragment(), args = bundle)

                }
            } else if (responseUrl.contains(Const.API_CATEGORY_GET_PRICE)) {
                if (responseCode == Const.STATUS_OK) {
                    val data = Gson().fromJson<MyServiceData>(jsonObjects.getJSONObject("detail").toString(), MyServiceData::class.java)
                    priceId = data.id
                    binding!!.priceTV.text = baseActivity!!.getString(R.string.euro) + data!!.price.toFloat().toString() + "/h"
                    binding!!.priceET.setText(data.price.toFloat().toString())
                } else{

                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.addTV -> {
                if (binding!!.priceET.checkString().isEmpty()) {
                    showToastOne("Please enter price")
                } else if (binding!!.priceET.checkString().toDouble() == 0.0) {
                    showToastOne("Please enter valid price")
                } else {
                    when {
                        subData != null -> {
                            if (subData!!.isCheck) {
                                hitUpdatePriceApi()
                            } else {
                                hitAddPriceApi()
                            }
                        }
                        else -> hitUpdatePriceApi()
                    }

                }


            }


        }
    }
}
