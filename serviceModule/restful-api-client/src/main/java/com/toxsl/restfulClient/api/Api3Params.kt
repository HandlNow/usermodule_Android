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

import android.net.Uri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class Api3Params {
    private var requestParentMap: HashMap<String, Any>? = null
    private var requestChildMap: HashMap<String, Any?>? = null

    private var requestMultipartChildMap: HashMap<String, RequestBody>? = null
    private var requestMultipartUriChildMap: HashMap<String, Uri>? = null

    val childObject: JSONObject
        get() = JSONObject(requestChildMap!!)

    init {
        requestParentMap = HashMap()
        requestChildMap = HashMap()
        requestMultipartChildMap = HashMap()
        requestMultipartUriChildMap = HashMap()
    }

    fun put(key: String, value: String?) {
        requestChildMap?.put(key, value)
    }

    fun put(key: String, value: Float) {
        requestChildMap?.put(key, value.toString())
    }

    fun put(key: String, value: Double) {
        requestChildMap?.put(key, value)
    }

    fun put(key: String, value: Int) {
        requestChildMap?.put(key, value)
    }

    fun put(key: String, value: JSONArray) {
        requestChildMap?.put(key, value)
    }

    fun put(key: String, value: Uri) {
        requestMultipartUriChildMap?.put(key, value)
    }

    fun putMultipart(key: String, value: String) {
        requestMultipartChildMap?.put(key, toRequestBody(value))
    }

    fun getServerObject(parent: String): JSONObject {
        requestParentMap?.put(parent, childObject)
        return JSONObject(requestParentMap as Map<*, *>)
    }

    fun getServerHashMap(): HashMap<String, Any?>? {
        return requestChildMap
    }

    fun getServerMultipartUriHashMap(): HashMap<String, Uri>? {
        return requestMultipartUriChildMap
    }

    fun getServerMultipartHashMap(): HashMap<String, RequestBody>? {
        return requestMultipartChildMap
    }

    // This method  converts String to RequestBody
    private fun toRequestBody(value: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), value)
    }

    // This method  converts String to RequestBody
    fun toRequestImage(key: String, file: File): MultipartBody.Part {
        val fileReqBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val part = MultipartBody.Part.createFormData(key, file.name, fileReqBody)

        return part
    }
}