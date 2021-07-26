/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.toxsl.restfulClient.api.extension

import android.util.Log
import com.toxsl.restfulClient.BuildConfig

fun log(title: String, message: String) {
    if (BuildConfig.DEBUG) {
        Log.e(title, message)
    }
}

fun handleException(e: Exception) {
    if (BuildConfig.DEBUG) {
        e.printStackTrace()
    }
}