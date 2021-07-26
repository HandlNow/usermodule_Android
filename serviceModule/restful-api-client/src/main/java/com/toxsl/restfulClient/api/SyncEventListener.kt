/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.toxsl.restfulClient.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


interface SyncEventListener {

    fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?)

    fun onSyncFailure(errorCode: Int, t: Throwable?, response: Response<String>?, call: Call<String>?, callBack: Callback<String>?)

    fun onSyncStart()

    fun onSyncFinish()


}