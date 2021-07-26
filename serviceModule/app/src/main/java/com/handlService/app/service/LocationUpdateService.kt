/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.service


import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.handlService.app.BuildConfig
import com.handlService.app.utils.API
import com.handlService.app.utils.Const
import com.handlService.app.utils.PrefStore
import com.toxsl.restfulClient.api.Api3Params
import com.toxsl.restfulClient.api.RestFullClient
import com.toxsl.restfulClient.api.SyncEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class LocationUpdateService : Service(), SyncEventListener {
    private var mLocationRequest: LocationRequest? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback? = null
    private var restFullClient: RestFullClient? = null
    private var apiInstance: Retrofit? = null
    private var api: API? = null
    private var store: PrefStore? = null
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    onNewLocation(location)
                }
            }
        }
        store = PrefStore(this)
        createLocationRequest()
        getLastLocation()
        initApi()
    }

    private fun onNewLocation(location: Location?) {
        log("mLocationReceived>>${mLocationReceived == null}")
        mLocationReceived?.onLocationReceived(location)
        location?.let {
            val params = Api3Params()
            params.put("User[latitude]", "" + location.latitude)
            params.put("User[longitude]", "" + location.longitude)
            val call = api!!.hitUpdateCurrentLocation(params.getServerHashMap())
            restFullClient?.sendRequest(call, this)
        }


    }

    private fun initApi() {
        restFullClient = RestFullClient.getInstance(this)
        apiInstance = if (BuildConfig.DEBUG) {
            restFullClient!!.getRetrofitInstance(Const.SERVER_REMOTE_URL)
        } else {
            restFullClient!!.getRetrofitInstance(Const.LIVE_SERVER_REMOTE_URL)
        }
        api = apiInstance!!.create(API::class.java)
    }

    /**
     * Sets the location request parameters.
     */
    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }


    override fun onDestroy() {
        val client = LocationServices.getFusedLocationProviderClient(this)
        client.removeLocationUpdates(mLocationCallback!!)
    }

    private fun getLastLocation() {
        fusedLocationClient?.let { fusedLocationClient ->
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            fusedLocationClient.lastLocation
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result != null) {
                            log("dsfsfgsdgsdgsd")
                        } else {
                            logStack(task.exception)
                        }
                    }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun requestLocationUpdates() {
        try {

            fusedLocationClient?.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            logStack(unlikely)
        }
    }


    private fun log(message: String) {
        if (BuildConfig.DEBUG) {
            Log.e("LOCATION_UPDATE>>", message)
        }
    }

    private fun logStack(e: Exception?) {
        if (BuildConfig.DEBUG) {
            e?.printStackTrace()
        }
    }

    fun setLocationReceivedListener(locationReceived: OnLocationReceived) {
        mLocationReceived = locationReceived
    }

    interface OnLocationReceived {
        fun onLocationReceived(location: Location?)
    }


    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {

    }

    override fun onSyncFailure(errorCode: Int, t: Throwable?, response: Response<String>?, call: Call<String>?, callBack: Callback<String>?) {
    }

    override fun onSyncStart() {
    }

    override fun onSyncFinish() {
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        requestLocationUpdates()
        return super.onStartCommand(intent, flags, startId)

    }

    fun startService(context: Context) {
        val callIntent = Intent(context, LocationUpdateService::class.java)
        context.startService(callIntent)
    }

    fun stopService(context: Context?) {
        val callIntent = Intent(context, LocationUpdateService::class.java)
        context!!.stopService(callIntent)
    }

    companion object {
        const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 10000
        const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
        private var mLocationReceived: OnLocationReceived? = null
        private var instance: LocationUpdateService? = null

        fun getInstance(): LocationUpdateService {
            if (instance == null) {
                instance = LocationUpdateService()
            }
            return instance as LocationUpdateService
        }


    }

}
