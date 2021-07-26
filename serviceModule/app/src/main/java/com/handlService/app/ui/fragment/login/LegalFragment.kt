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
import android.text.Html
import android.view.*
import androidx.databinding.DataBindingUtil
import com.handlService.app.R
import com.handlService.app.databinding.FragmentLegalBinding
import com.handlService.app.ui.activity.MainActivity
import com.handlService.app.ui.fragment.BaseFragment
import com.handlService.app.utils.Const
import org.json.JSONObject


class LegalFragment : BaseFragment() {

    private var binding: FragmentLegalBinding? = null
    private var type = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_legal, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getInt("type") as Int
    }

    private fun initUI() {
        baseActivity!!.setToolbar(title = getString(R.string.legal))
        when (type) {
            Const.TERMS -> {
                binding!!.textTV.text = baseActivity!!.getString(R.string.terms_amp_conditions)
            }
            Const.PRIVACY -> {
                binding!!.textTV.text = baseActivity!!.getString(R.string.privacy_policy)
            }
            Const.ABOUT -> {
                binding!!.textTV.text = baseActivity!!.getString(R.string.about_us)
            }
            else -> {
                baseActivity!!.setToolbar(title = getString(R.string.legal))
                binding!!.textTV.text = baseActivity!!.getString(R.string.legal)
            }
        }
        hitPageAPI()

        if (baseActivity is MainActivity) {
            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.homeMB) {
            (baseActivity as MainActivity).openDrawer()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hitPageAPI() {
        val call = api!!.apiGetStaticPage(type)
        restFullClient!!.sendRequest(call, this)

    }


    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_STATIC_PAGE)) {
                if (responseCode == Const.STATUS_OK) {
                    val obj = jsonObjects.getJSONObject(Const.LIST)
                    binding!!.textTV.text = Html.fromHtml(obj.optString("title"))
                    binding!!.descTV.text = Html.fromHtml(obj.optString("description"))

                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }


}
