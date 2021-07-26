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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.handlUser.app.BuildConfig
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentAddPaymentMethodBinding
import com.handlUser.app.ui.activity.LoginSignUpActivity
import com.handlUser.app.utils.Const
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Token
import com.stripe.android.view.CardValidCallback
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class AddPaymentMethodFragment : BaseFragment(), CardValidCallback {

    private var binding: FragmentAddPaymentMethodBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_add_payment_method, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.dotsI.dot4.setImageResource(R.drawable.circle_dark_blue)
        baseActivity!!.setToolbar(title = getString(R.string.add_details))
        if (baseActivity is LoginSignUpActivity) {
            binding!!.dotsI.dot4.setImageResource(R.drawable.circle_dark_blue)
            binding!!.logoIV.visibility = View.VISIBLE
        } else {
            binding!!.logoIV.visibility = View.GONE
        }

        binding!!.cardNumberEditText.setCardValidCallback(this)
    }


    private fun hitCardAPI(result: Token) {
        val param = Api3Params()
        param.put("token", result.id)
        val call = api!!.apiAddCard(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonobject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_CARD_ADD)) {
                if (responseCode == Const.STATUS_OK) {
                    showToast(jsonobject.optString("message"))
                    baseActivity!!.onBackPressed()
                }
            }

        } catch (e: Exception) {
            handleException(e)
        }
    }

    override fun onInputChanged(isValid: Boolean, invalidFields: Set<CardValidCallback.Fields>) {
        if (isValid) {
            binding!!.doneTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.light_blue_button)
            binding!!.doneTV.setOnClickListener {
                getCardDetails()
            }
        } else {
            binding!!.doneTV.background = ContextCompat.getDrawable(baseActivity!!, R.drawable.grey_button)
        }
    }

    private fun getCardDetails() {
        val card = binding!!.cardNumberEditText.cardParams
        if (card != null) {
            baseActivity!!.onSyncStart()
            val stripe = Stripe(baseActivity!!, Const.STRIPE_PUBLISH_KEY)
            stripe.createCardToken(card,
                    callback = object : ApiResultCallback<Token> {
                        override fun onError(e: Exception) {
                            baseActivity!!.onSyncFinish()
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace()
                            }
                        }

                        override fun onSuccess(result: Token) {
                            baseActivity!!.onSyncFinish()
                            if (BuildConfig.DEBUG) {
                                log("token" + result.id)
                            }
                            hitCardAPI(result)

                        }

                    }
            )
        } else {
            baseActivity!!.showToastOne(getString(R.string.invalid_card_details))
        }

    }

}
