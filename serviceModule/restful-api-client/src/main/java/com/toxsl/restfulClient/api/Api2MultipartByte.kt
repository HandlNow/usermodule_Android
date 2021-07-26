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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

class Api2MultipartByte {
    var builder: MultipartBody.Builder? = null

    init {
        builder = MultipartBody.Builder().setType(MultipartBody.FORM)
    }


    fun put(key: String, value: String) {
        builder!!.addFormDataPart(key, value)
    }

    fun put(key: String, value: Int) {
        builder!!.addFormDataPart(key, value.toString())
    }

    fun putByte(key: String, featured_image: File) {
        if (featured_image.exists()) { // If you want to use Bitmap then use this
            val bmp: Bitmap = BitmapFactory.decodeFile(featured_image.absolutePath)
            val bos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 30, bos)
            builder!!.addFormDataPart(
                    key,
                    featured_image.name,
                    RequestBody.create(MultipartBody.FORM, bos.toByteArray())
            )
        }
    }

    fun put(key: String, value: JSONArray) {
        builder!!.addFormDataPart(key, value.toString())
    }

    fun put(key: String, file: File?) {
        if (file != null && file.exists()) {
            builder!!.addFormDataPart(key, file.name, RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file));
        } else {
            throw FileNotFoundException()
        }

    }


    fun getRequestBody(): RequestBody {
        val requestBody: RequestBody = builder!!.build()
        return requestBody
    }


}