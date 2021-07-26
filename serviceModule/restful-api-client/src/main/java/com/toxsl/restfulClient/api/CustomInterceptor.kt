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

import android.content.Context
import com.toxsl.restfulClient.BuildConfig
import com.toxsl.restfulClient.api.extension.log
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset


internal class CustomInterceptor(var mContext: Context) : Interceptor {
    private val UTF8 = Charset.forName("UTF-8")
    private val PREFS_NAME = "RestFullClient"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request_ = chain.request()
        var response: Response? = null
        showBodyData(request_)
        val value = getLoginStatus()
        if (value != null) {
            val originalUrl: HttpUrl = request_.url
            val url = originalUrl.newBuilder().addQueryParameter("access-token", value).build()
            val requestBuilder: Request.Builder = request_.newBuilder().addHeader("Authorization", "Bearer " + value).url(url)
            val request: Request = requestBuilder.build()
            response = chain.proceed(request)
        } else {
            response = chain.proceed(request_)
        }

        if (response!!.code == 301 || response.code == 302 || response.code == 303 || response.code == 307 || response.code == 308) {
            var request1 = request_.newBuilder().url(response.header("Location")!!).addHeader("Authorization", "Bearer " + value).build()
            val requestBuilder: Request.Builder =
                    request_.newBuilder().addHeader("Authorization", "Bearer " + value)/*.url(url)*/
//                            request1 = requestBuilder.build()

            response = chain.proceed(request1)
        }

        return response
    }


    private fun showBodyData(request: Request) {
        val requestBody: RequestBody? = request.body
        if (requestBody != null) {
            val buffer = Buffer()
            requestBody.writeTo(buffer)

            var charset: Charset? = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            try {
                log("Request_params : ", URLDecoder.decode(buffer.readString(charset!!)).replace("&", "\n"))
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

        }
    }


    fun getLoginStatus(): String? {
        val settings = mContext!!.getSharedPreferences(PREFS_NAME, 0)
        return settings.getString("access_token", null)
    }

}


