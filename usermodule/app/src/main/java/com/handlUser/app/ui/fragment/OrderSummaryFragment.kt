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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentOrderSummaryBinding
import com.handlUser.app.model.AppointmentData
import com.handlUser.app.model.ChatData
import com.handlUser.app.ui.extensions.checkString
import com.handlUser.app.ui.extensions.loadFromUrl
import com.handlUser.app.ui.extensions.makeEditScrollableInsideScrollView
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject
import kotlin.math.cos


class OrderSummaryFragment : BaseFragment(), ClickHandler {

    private var binding: FragmentOrderSummaryBinding? = null
    private var data: AppointmentData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            data = arguments?.getParcelable<AppointmentData>("booking_data")
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_order_summary, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        binding!!.handleClick = this
        baseActivity!!.setToolbar(getString(R.string.your_service_done), ContextCompat.getColor(baseActivity!!, R.color.White))
        resetIcons()
        binding!!.reviewET.makeEditScrollableInsideScrollView()

        hitOrderSummaryAPI()
    }

    private fun hitOrderSummaryAPI() {
        val call = api!!.apiGetOrderSummary(data!!.id)
        restFullClient!!.sendRequest(call, this)
    }

    private fun resetIcons() {
        binding!!.complementTV.visibility = View.VISIBLE
        binding!!.complimentET.visibility = View.GONE
        binding!!.orTV.visibility = View.VISIBLE
        binding!!.reviewTV.visibility = View.VISIBLE
        binding!!.reportTV.visibility = View.VISIBLE
        binding!!.reviewET.visibility = View.GONE
        binding!!.reportET.visibility = View.GONE
    }


    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.submitTV -> {
                when {
                    binding!!.ratingRB.rating == 0f -> {
                        showToastOne(baseActivity!!.getString(R.string.please_add_rating))
                    }
                    binding!!.reviewET.checkString().isEmpty() && binding!!.complimentET.checkString().isEmpty() -> {
                        showToastOne(baseActivity!!.getString(R.string.please_add_reviews))
                    }
                    else -> {
                        hitRatingAPI()
                    }
                }
            }
            R.id.reportTV -> {
                resetIcons()
                binding!!.reportTV.visibility = View.GONE
                binding!!.reportET.visibility = View.VISIBLE
            }
            R.id.reviewTV -> {
                resetIcons()
                binding!!.orTV.visibility = View.GONE
                binding!!.reviewTV.visibility = View.GONE
                binding!!.reviewET.visibility = View.VISIBLE
            }
            R.id.complementTV -> {
                resetIcons()
                binding!!.orTV.visibility = View.GONE
                binding!!.complementTV.visibility = View.GONE
                binding!!.complimentET.visibility = View.VISIBLE


            }
        }
    }

    private fun hitRatingAPI() {
        val params = Api3Params()
        params.put("Rating[model_id]", data!!.id)
        params.put("Rating[booking_id]", data!!.id)
        params.put("Rating[rating]", binding!!.ratingRB.rating)
        params.put("Rating[title]", binding!!.reviewET.checkString())
        params.put("Rating[comment]", binding!!.complimentET.checkString())
        params.put("Rating[created_by_id]", baseActivity!!.getProfileData()!!.id!!)

        val call = api!!.apiAddRating(params.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    private fun hitReportProblemAPI() {
        val params = Api3Params()
        params.put("ReportProblem[booking_id]", data!!.id)
        params.put("ReportProblem[title]", binding!!.reportET.checkString())
        params.put("ReportProblem[provider_id]", baseActivity!!.getProfileData()!!.id!!)

        val call = api!!.apiReportProblem(params.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            if (responseCode == Const.STATUS_OK) {
                val jsonobject = JSONObject(response!!)
                if (responseUrl.contains(Const.API_ORDER_SUMMARY)) {
                    if (responseCode == Const.STATUS_OK) {
                        data = Gson().fromJson(jsonobject.getJSONObject("detail").toString(), AppointmentData::class.java)
                        setData()
                    }
                } else if (responseUrl.contains(Const.API_ADD_RATING)) {
                    if (responseCode == Const.STATUS_OK) {
                        showToastOne(baseActivity!!.getString(R.string.rating_add_success))
                        if (binding!!.reportET.checkString().isNotEmpty()) {
                            baseActivity!!.replaceFragment(PaymentMethodListFragment(), requireArguments())
                        } else {
                            hitReportProblemAPI()
                        }
                    }
                } else if (responseUrl.contains(Const.API_REPORT_PROBLEM)) {
                    if (responseCode == Const.STATUS_OK) {
                        baseActivity!!.replaceFragment(PaymentMethodListFragment(), requireArguments())
                    }
                }

            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        val dur = (data!!.bookingSlots.size.toFloat() / 2).toString()
        val arr = dur.split(".")
        var text = ""
        if (arr[0].toInt() > 0) {
            text = text + arr[0] + baseActivity!!.getString(R.string.hours)
        }
        if (arr[1].toInt() == 5) {
            text = text + Const.THIRTY.toString() + " " + baseActivity!!.getString(R.string.minutes)
        }

        binding!!.durationTextTV.text = text
        binding!!.costTextTV.text = baseActivity!!.getString(R.string.euro) + ((data!!.price.toFloat() * 2) / data!!.bookingSlots.size).toString()
        binding!!.fcostTextTV.text = baseActivity!!.getString(R.string.euro) + data!!.price.toFloat().toString()
        binding!!.calculationTV.text = baseActivity!!.getString(R.string.euro) + data!!.price.toFloat().toString()
        binding!!.calculateTV.text = "Total(" + text +")"

        binding!!.rateTV.text = baseActivity!!.getString(R.string.rate) + " " + data!!.provider.fullName
        binding!!.profilePicCIV.loadFromUrl(baseActivity!!, data!!.provider.profileFile)


        if (data!!.isRate) {
            if (data!!.paymentStatus == Const.ONE) {
                binding!!.paidTV.visibility = View.VISIBLE
                binding!!.submitTV.visibility = View.GONE
            }
            binding!!.ratingRB.rating = data!!.rating!!.rating.toFloat()
            binding!!.complimentET.setText(data!!.rating!!.comment)
            binding!!.reviewET.setText(data!!.rating!!.title)
            binding!!.reviewET.isFocusable = false
            binding!!.reviewET.isFocusableInTouchMode = false
            binding!!.reviewET.isLongClickable = false
            binding!!.complimentET.isFocusable = false
            binding!!.complimentET.isFocusableInTouchMode = false
            binding!!.complimentET.isLongClickable = false
            binding!!.ratingRB.setIsIndicator(true)
        }
    }

}
