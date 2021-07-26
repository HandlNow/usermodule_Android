/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.activity

import android.view.View
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.handlService.app.BuildConfig
import com.handlService.app.utils.API
import com.handlService.app.utils.Const
import com.handlService.app.utils.PrefStore
import com.handlService.app.utils.TypefaceUtil
import com.toxsl.restfulClient.api.RestFullClient
import retrofit2.Retrofit


class BaseApp : MultiDexApplication() {
    var store: PrefStore? = null
    var restFullClient: RestFullClient? = null
    var apiInstance: Retrofit? = null
    var api: API? = null
    var myView: View? = null
    var openTokKey: String? = null
    var openTokSessionID: String? = null
    var openTokId: String? = null
    var openTokToken: String? = null
    var name: String? = null
    var profileImage: String? = null
    var callType = 0
    fun initSyncManager(): RestFullClient {
        restFullClient = RestFullClient.getInstance(this)
        apiInstance = if (BuildConfig.DEBUG) {
            restFullClient!!.getRetrofitInstance(Const.SERVER_REMOTE_URL)
        } else {
            restFullClient!!.getRetrofitInstance(Const.LIVE_SERVER_REMOTE_URL)
        }
        api = apiInstance!!.create(API::class.java)

        return restFullClient!!
    }


    fun getRestClient(): RestFullClient? {

        return if (restFullClient == null) initSyncManager() else restFullClient
    }

    fun getPrefStore(): PrefStore {
        return initPrefStore()
    }

    fun getAPI(): API? {
        return initApi()
    }


    private fun initPrefStore(): PrefStore {
        if (store == null) {
            store = PrefStore(this)
        }
        return store!!
    }

    private fun initApi(): API {
        if (api == null) {
            api = apiInstance!!.create(API::class.java)
        }
        return api!!
    }

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        //  This FontsOverride comes from the example I posted above
        TypefaceUtil.overrideFont(applicationContext, "SERIF", "fonts/lato_regular.ttf")

    }

}