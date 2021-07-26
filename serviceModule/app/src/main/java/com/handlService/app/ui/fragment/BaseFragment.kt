/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.handlService.app.BuildConfig
import com.handlService.app.R
import com.handlService.app.ui.activity.BaseActivity
import com.handlService.app.utils.API
import com.handlService.app.utils.Const
import com.handlService.app.utils.PrefStore
import com.toxsl.restfulClient.api.RestFullClient
import com.toxsl.restfulClient.api.SyncEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import toxsl.imagebottompicker.ImageBottomPicker
import java.util.*


open class BaseFragment : Fragment(), AdapterView.OnItemClickListener, View.OnClickListener, SyncEventListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, ImageBottomPicker.OnImageSelectedListener {

    var baseActivity: BaseActivity? = null
    var store: PrefStore? = null
    var restFullClient: RestFullClient? = null
    var api: API? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = activity as BaseActivity?
        if (baseActivity!!.restFullClient == null) {
            baseActivity!!.restFullClient = RestFullClient.getInstance(baseActivity!!)
            baseActivity!!.apiInstance = if (BuildConfig.DEBUG) {
                baseActivity!!.restFullClient!!.getRetrofitInstance(Const.SERVER_REMOTE_URL)
            } else {
                baseActivity!!.restFullClient!!.getRetrofitInstance(Const.LIVE_SERVER_REMOTE_URL)
            }
            baseActivity!!.api = baseActivity!!.apiInstance!!.create(API::class.java)
        }
        restFullClient = baseActivity!!.restFullClient
        api = baseActivity!!.api
        store = baseActivity!!.store

    }

    fun searchLocation(requestCode: Int) {
        // Set the fields to specify which types of place data to return.
        try {
            val fields = listOf(Place.Field.ID,
                    Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS)
            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(baseActivity!!)
            baseActivity!!.startActivityForResult(intent, requestCode)

        } catch (e: GooglePlayServicesNotAvailableException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        baseActivity!!.hideSoftKeyboard()
        activity!!.invalidateOptionsMenu()
    }

    override fun onClick(v: View) {

    }

    fun showToast(msg: String) {
        baseActivity!!.showToastOne(msg)
    }

    fun showToastOne(s: String) {
        baseActivity!!.showToastOne(s)
    }

    override fun onSyncStart() {
        baseActivity!!.onSyncStart()
    }

    override fun onSyncFinish() {
        baseActivity!!.onSyncFinish()
    }

    override fun onSyncFailure(errorCode: Int, t: Throwable?, response: Response<String>?, call: Call<String>?, callBack: Callback<String>?) {
        baseActivity!!.onSyncFailure(errorCode, t, response, call, callBack)
    }

    fun log(s: String) {
        baseActivity!!.log(s)
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {

    }

    fun chooseImagePicker(requestCode: Int, showRemove: Boolean) {
        val bottomPicker = ImageBottomPicker.Builder(baseActivity!!, requestCode)
                .setOnImageSelectedListener(this).showRemoved(showRemove)
                .create()
        bottomPicker.show(baseActivity?.supportFragmentManager)
    }

    override fun onImageSelected(uri: Uri?, requestCode: Int) {

    }

    override fun onImageRemoved(isRemoved: Boolean, requestCode: Int) {
    }


}
