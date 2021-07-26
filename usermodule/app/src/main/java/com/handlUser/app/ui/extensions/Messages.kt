/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.extensions

import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.handlUser.app.BuildConfig
import com.handlUser.app.R
import com.handlUser.app.ui.activity.BaseActivity
import com.toxsl.restfulClient.api.extension.handleException
import com.toxsl.restfulClient.api.extension.log
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun getMessage(json: JSONObject, baseActivity: BaseActivity) {
    var string: String
    string = baseActivity.getString(R.string.success_msg)
    if (json.has("message")) {
        string = json.getString("message")
    }
    showToastMain(baseActivity, string)
}

fun getErrorMsg(json: JSONObject, baseActivity: BaseActivity) {
    var string: String
    string = baseActivity.getString(R.string.soemthing_went_wrong)
    if (json.has("error")) {
        string = json.getString("error")
    }
    showToastMain(baseActivity, string)
}

fun showToastMain(baseActivity: BaseActivity, string: String) {
    Toast.makeText(baseActivity, string, Toast.LENGTH_SHORT).show()
}

fun TextView.setColor(baseActivity: BaseActivity?, lightGrey: Int) {
    this.setTextColor(ContextCompat.getColor(baseActivity!!, lightGrey))
}

fun TextView.setbackground(baseActivity: BaseActivity?, lightGrey: Int) {
    this.background = ContextCompat.getDrawable(baseActivity!!, lightGrey)
}


fun changeDateFormat(dateString: String): String {
    if (dateString.isEmpty()) {
        return ""
    }
    val inputDateFromat = SimpleDateFormat("yyyy-MM-DD HH:MM:SS", Locale.getDefault())
    var date = Date()
    try {
        date = inputDateFromat.parse(dateString)
    } catch (e: ParseException) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }

    val outputDateFormat = SimpleDateFormat("EEE, MMM d, ''yy h:mm a", Locale.getDefault())
    return outputDateFormat.format(date)
}

fun JSONObject.handleErrorForServer(setKey: String): String {
    var message = ""
    try {
        if (this.has("error") && this.getJSONObject("error").has(setKey)) {
            message = this.getJSONObject("error").getString(setKey).replace("[", "").replace("]", "").replace("[", "").replace("]", "")
        }
    } catch (e: JSONException) {
        handleException(e)
    }

    return message
}

fun JSONObject.handleMessageForServer(setKey: String): String {
    var message = ""
    try {
        if (this.has("message") && this.getJSONObject("message").has(setKey)) {
            message = this.getJSONObject("message").getString(setKey).replace("[", "").replace("]", "").replace("[", "").replace("]", "")
        }
    } catch (e: JSONException) {
        handleException(e)
    }

    return message
}

fun createArrayList(): ArrayList<String> {
    val array: ArrayList<String> = arrayListOf()
    for (i in 1 until 21) {
        array.add(i.toString())
    }
    return array
}


