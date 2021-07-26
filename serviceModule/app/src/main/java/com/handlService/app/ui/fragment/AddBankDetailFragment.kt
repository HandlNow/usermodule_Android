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
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.databinding.DataBindingUtil
import com.handlService.app.R
import com.handlService.app.databinding.FragmentAddBankdetailBinding
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.login.VerificationFragment
import com.handlService.app.utils.Const
import com.handlService.app.utils.CustomJavaScriptInterFace
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.extension.handleException
import org.json.JSONObject


class AddBankDetailFragment : BaseFragment() {


    private var binding: FragmentAddBankdetailBinding? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        if (binding == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(baseActivity), R.layout.fragment_add_bankdetail, container, false)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        baseActivity!!.setToolbar(title = getString(R.string.payment_method))
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun init() {
        if (baseActivity!!.isNetworkAvailable) {
            binding!!.webViewWV.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            binding!!.webViewWV.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            binding!!.webViewWV.settings.setAppCacheEnabled(false)
            binding!!.webViewWV.clearCache(true)
            binding!!.webViewWV.settings.useWideViewPort = true
            binding!!.webViewWV.settings.javaScriptEnabled = true
            binding!!.webViewWV.settings.domStorageEnabled = true
            binding!!.webViewWV.measure(100, 100)
            binding!!.webViewWV.settings.useWideViewPort = true
            binding!!.webViewWV.settings.loadWithOverviewMode = true
            binding!!.webViewWV.addJavascriptInterface(CustomJavaScriptInterFace(baseActivity!!), "HtmlViewer")
            binding!!.webViewWV.webViewClient = object : WebViewClient() {
                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    baseActivity!!.startProgressDialog()
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    view.loadUrl("javascript:HtmlViewer.showHTML" +
                            "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
                    view.evaluateJavascript("(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();", object : ValueCallBack(url) {


                        override fun onReceiveValue(value: String?) {
                            if (value!!.contains(baseActivity!!.getString(R.string.your_account_has_been_created_successfully))) {
                                hitAddressAPI(url)
                            }
                        }
                    })
                    baseActivity!!.stopProgressDialog()
                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return false
                }

                override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    baseActivity!!.showToastOne(description)
                    baseActivity!!.stopProgressDialog()
                }

                override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                    super.onReceivedSslError(view, handler, error)
                    baseActivity!!.log(error.toString())
                }
            }
            binding!!.webViewWV.loadUrl(baseActivity!!.getProfileData()!!.addBankWebViewUrl)
        } else {
            binding!!.webViewWV.loadData("""<!DOCTYPE html>
<html>
<head>
<title>${baseActivity!!.getString(R.string.errors)}</title>
</head>
<body>

<h1>${baseActivity!!.getString(R.string.no_internet_connection)}</h1>
<p>${baseActivity!!.getString(R.string.page_cannot_be_loaded)} </p>

</body>
</html>""", "text/html", "UTF-8")
        }
    }


    private fun hitAddressAPI(url: String) {
        val param = Api3Params()
        param.put("url", url)
        val call = api!!.apiBank(param.getServerHashMap())
        restFullClient!!.sendRequest(call, this)
    }

    internal abstract class ValueCallBack(var url: String) : ValueCallback<String?>

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_ADD_BANK)) {
                if (responseCode == Const.STATUS_OK) {
                    baseActivity!!.showToastOne(jsonObjects.optString("message"))
                    baseActivity!!.replaceFragment(VerificationFragment())
                }
            }
        } catch (e: java.lang.Exception) {
            handleException(e)
        }
    }


}