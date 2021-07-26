/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.utils
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.libraries.places.api.model.Place
import com.handlService.app.BuildConfig
import com.handlService.app.R
import com.handlService.app.model.AddressData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class  AddressDecoder private constructor(private val context: Context) {
    private var addressListener: AddressListener? = null

    private fun log(s: String?) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, s.toString())
        }
    }


    @Synchronized
    suspend fun getAddressDataAsync(latitude: Double, longitude: Double, place: Place?) {
        val data = GlobalScope.async {

            getAddressData(latitude, longitude)
        }.await()
        place?.let {
            data?.address = place.address
        }
        onAddressFound(data)
    }


    fun getAddressData(latitude: Double, longitude: Double): AddressData? {
        val addressData = AddressData()
        addressData.latitude = latitude.toString()
        addressData.longitude = longitude.toString()

        // Get the coordinates from your place
        val geocoder = Geocoder(context)
        val addresses: List<Address> // Only retrieve 1 address
        addresses = try {
            geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1)
        } catch (e: IOException) {
            logStack(e)
            return null
        }
        return if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]

            log("state=$address")
            addressData.city = address.subAdminArea ?: address.locality ?: ""
            log("state=${address.adminArea}") // state
            addressData.state = address.adminArea ?: ""
            log("country=${address.countryName}") // country
            addressData.country = address.countryName ?: ""
            addressData.zipcode = address.postalCode ?: ""
            log("zipcode=${address.postalCode}") // zipcode
            addressData.isZoomMap = false
            val fullAddress = address.getAddressLine(0) ?: ""
            log("Address >>>$fullAddress")
            addressData.address = fullAddress
            addressData

        } else  {
            try {
                val json = getJSONFromURL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true&key=" + context.getString(R.string.google_api))
                val address: String
                if (json!!.has("results") && json.getJSONArray("results").length() > 0) {
                    address = json.getJSONArray("results").getJSONObject(0).getString("formatted_address")
                    val addressComponents = json.getJSONArray("results").getJSONObject(0).getJSONArray("address_components")

                    for (k in 0 until addressComponents.length()) {
                        if (addressComponents.getJSONObject(k).has("types")) {
                            val typeArray = addressComponents.getJSONObject(k).getJSONArray("types")
                            for (m in 0 until typeArray.length()) {
                                if (typeArray.getString(m) == "postal_code") {
                                    addressData.zipcode = (addressComponents.getJSONObject(k).getString("long_name"))

                                } else if (typeArray.getString(m) == "country") {
                                    addressData.country = addressComponents.getJSONObject(k).getString("long_name")
                                } else if (typeArray.getString(m) == "administrative_area_level_1") {
                                    addressData.state = addressComponents.getJSONObject(k).getString("long_name")

                                }
                            }
                        }
                    }
                    addressData.address = address
                    return addressData
                }
            } catch (e: Exception) {
                logStack(e)
            }
            null
        }
    }

    private fun getJSONFromURL(url: String): JSONObject? {
        log(url)
        log("response>>>v${URL(url).readText()}")
        return JSONObject(URL(url).readText())

    }

    fun saveDataFromPlace(place: Place) {
        val addressData = getAddressData(place.latLng!!.latitude, place.latLng!!.longitude)
        addressData!!.address = place.name
        addressData.isZoomMap = true
        onAddressFound(addressData)
    }

    fun getLastKnownLocation(context: Context): Location? {
        val MIN_TIME_BW_UPDATES: Long = 1000
        val MIN_DISTANCE_CHANGE_FOR_UPDATES = 10f
        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, R.string.location_not_specified, Toast.LENGTH_LONG).show()
                return bestLocation
            }
            var l = mLocationManager.getLastKnownLocation(provider)
            if (l == null) {
                mLocationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, object : LocationListener {
                    override fun onLocationChanged(location: Location) {}
                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                })
                l = mLocationManager.getLastKnownLocation(provider)
            }
            if (l != null && (bestLocation == null || l.accuracy > bestLocation.accuracy)) {
                bestLocation = l
            }
        }
        if (bestLocation == null && !isGPSEnabled(context)) {
            Toast.makeText(context, R.string.gps_not_enabled, Toast.LENGTH_LONG).show()
        }
        return bestLocation
    }


    private fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    interface AddressListener {
        fun onPlacesAddressFound(addressData: AddressData?)
    }

    fun setAddressListener(addressListener: AddressListener?) {
        this.addressListener = addressListener
    }

    private fun onAddressFound(addressData: AddressData?) {
        if (addressListener != null) {
            addressListener!!.onPlacesAddressFound(addressData)
        }
    }

    companion object {
        private val TAG = AddressDecoder::class.java.simpleName

        @SuppressLint("StaticFieldLeak")
        private var instance: AddressDecoder? = null

        fun getInstance(context: Context): AddressDecoder? {
            if (instance == null) {
                instance = AddressDecoder(context)
            }
            return instance
        }
    }

    fun logStack(e: Exception) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }

}