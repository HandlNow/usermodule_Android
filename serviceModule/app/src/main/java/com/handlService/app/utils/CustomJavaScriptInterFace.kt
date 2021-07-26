/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.utils

import android.webkit.JavascriptInterface
import com.handlService.app.ui.activity.BaseActivity

class CustomJavaScriptInterFace(private val baseActivity: BaseActivity) {
    @JavascriptInterface
    fun showHTML(html: String?) {
        println(html)
    }

}