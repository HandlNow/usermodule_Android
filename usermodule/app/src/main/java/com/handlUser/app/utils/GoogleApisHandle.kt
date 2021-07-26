/*
* @copyright : TOXSL Technologies Pvt. Ltd. < www.toxsl.com >
* @author     : Shiv Charan Panjeta < shiv@toxsl.com >
* All Rights Reserved.
* Proprietary and confidential :  All information contained herein is, and remains
* the property of TOXSL Technologies Pvt. Ltd. and its partners.
* Unauthorized copying of this file, via any medium is strictly prohibited.
*/

package com.handlUser.app.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.handlUser.app.BuildConfig
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogLocationTurnonBinding
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Double.parseDouble
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.math.ceil


class GoogleApisHandle {
    private var routeMap: GoogleMap? = null
    internal var context: Context? = null
    private var origin: LatLng? = null
    private var destination: LatLng? = null
    private var isAddLine: Boolean = false
    private var polyline: Polyline? = null
    private var totalDistance: Double? = null
    private var distanceValue: String? = null
    private var totalDuration: String? = null
    private val locationListener: LocationListener? = null
    private var onPolyLineReceived: OnPolyLineReceived? = null
    private var value: Boolean = false

    private fun setAct(mAct: Context) {
        this.context = mAct
    }

    fun getBearingRotate(begin: LatLng, end: LatLng, marker: Marker): Float {
        val brng = bearingBetweenLocations(begin.latitude, begin.longitude, end.latitude, end.longitude)

        var rotation = marker.rotation

        if (rotation >= 360) {
            rotation %= 360
        } else if (rotation <= -360) {
            rotation %= 360
            rotation += 360
        }

        var newAngle = marker.rotation.toDouble()
        if (brng - rotation in 0.0..180.0) {
            newAngle = marker.rotation + (brng - rotation)
        } else if (brng - rotation >= 0 && brng - rotation >= 180) {
            newAngle = marker.rotation - (360 - (brng - rotation))
        } else if (brng - rotation <= 0 && brng - rotation >= -180) {
            newAngle = marker.rotation + brng - rotation
        } else if (brng - rotation <= 0 && brng - rotation <= -180) {
            newAngle = marker.rotation + (360 - (rotation - brng))
        }

        return newAngle.toFloat()
    }

    fun getAddress(lat: Double, lang: Double): String? {
        try {
            val geocoder: Geocoder = Geocoder(context)
            val addresses: List<Address>
            if (lat != 0.0 || lang != 0.0) {
                addresses = geocoder.getFromLocation(lat, lang, 1)
                val address = addresses[0].getAddressLine(0)
                val city = addresses[0].getAddressLine(1)
                val country = addresses[0].getAddressLine(2)
                val state = addresses[0].subLocality
                return address + ", " + city + ", " + (country ?: "")
            } else {
                return null
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return null
        }

    }


    fun decodeAddressFromLatLng(context: Context, latLng: LatLng): String {
        var address = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10)
            val json: JSONObject?
            if (addresses.size == 0) {
                json = getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true")
                try {
                    if (json!!.getJSONArray("results").length() > 0)
                        address = json.getJSONArray("results").getJSONObject(0).getString("formatted_address")
                } catch (e: JSONException) {
                    if (BuildConfig.DEBUG) {
                        e.printStackTrace()
                    }
                }

            } else {
                if (addresses[0].adminArea != null && addresses[0].countryName != null) {
                    address = addresses[0].getAddressLine(0) + "," + addresses[0].locality + "," + (if (addresses[0].adminArea.equals("Punyab", ignoreCase = true)) {
                        "Punjab"
                    } else {
                        addresses[0].adminArea
                    }) + "," + addresses[0].countryName // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                } else if (addresses[0].adminArea != null) {
                    address = addresses[0].getAddressLine(0) + "," + addresses[0].locality + "," + (if (addresses[0].adminArea.equals("Punyab", ignoreCase = true)) {
                        "Punjab"
                    } else {
                        addresses[0].adminArea
                    }) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                } else if (addresses[0].countryName != null) {
                    address = addresses[0].getAddressLine(0) + "," + addresses[0].locality + "," + addresses[0].countryName // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                } else {
                    address = addresses[0].getAddressLine(0) + "," + addresses[0].locality
                }
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        return address
    }


    fun getLatLngFromAddress(address: String): LatLng? {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val latLng = geocoder.getFromLocationName(address, 10)
            val location = latLng[0]
            if (location == null) {
                val `object` = getJSONfromURL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address.replace(" ", "%20") + "&key=" + context!!.getString(R.string.google_api))
                val jsonObject = `object`!!.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location")
                return LatLng(jsonObject.getDouble("lat"), jsonObject.getDouble("lng"))
            } else {
                return LatLng(location.latitude, location.longitude)
            }
        } catch (e: JSONException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        return null
    }

    private fun getJSONfromURL(url: String): JSONObject? {

        // initialize
        var `is`: InputStream? = null
        var result = ""
        var jObject: JSONObject? = null

        // http post
        try {
            val httpclient = DefaultHttpClient()
            val httppost = HttpPost(url)
            val response = httpclient.execute(httppost)
            val entity = response.entity
            `is` = entity.content

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("log_tag", "Error in http connection " + e.toString())
            }
        }

        // convert response to string
        try {
            val reader = BufferedReader(InputStreamReader(`is`!!, "iso-8859-1"), 8)
            val sb = StringBuilder()
            val line: String? = null
            while (true) {
                sb.append(line!! + "\n")
            }
            `is`.close()
            result = sb.toString()
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("log_tag", "Error converting result " + e.toString())

            }
        }

        // try parse the string to a JSON object
        try {
            jObject = JSONObject(result)
        } catch (e: JSONException) {
            if (BuildConfig.DEBUG) {
                Log.e("log_tag", "Error parsing data " + e.toString())
            }
        }

        return jObject
    }

    fun getLastKnownLocation(context: Context): Location? {
        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, context.getString(R.string.location_permission_not_specified), Toast.LENGTH_LONG).show()
                return bestLocation
            }
            var l: Location? = mLocationManager.getLastKnownLocation(provider)
            if (l == null) {
                mLocationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, object : LocationListener {
                    override fun onLocationChanged(location: Location) {

                    }

                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

                    }

                    override fun onProviderEnabled(provider: String) {

                    }

                    override fun onProviderDisabled(provider: String) {

                    }
                })
                l = mLocationManager.getLastKnownLocation(provider)
            }
            if (l != null && (bestLocation == null || l.accuracy > bestLocation.accuracy)) {
                bestLocation = l
            }
        }
        if (bestLocation == null && !isGPSEnabled(context)) {
            Toast.makeText(context, context.getString(R.string.gps_not_enabled), Toast.LENGTH_LONG).show()
        }

        return bestLocation
    }

    private fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun getDirectionsUrl(origin: LatLng, dest: LatLng, googleMap: GoogleMap, zoomValue: Boolean) {
        isAddLine = true
        value = zoomValue

        val str_origin = ("origin=" + origin.latitude + ","
                + origin.longitude)
        val str_dest = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false"
        val parameters = "$str_origin&$str_dest&$sensor"
        val output = "json"
        val url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + context!!.getString(R.string.google_api)
        DownloadTask(origin, dest, googleMap).execute(url)
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            data = urlConnection.inputStream.bufferedReader().readText()
            return data

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        return data
    }

    fun rotateMarker(fromLat: Double, fromLong: Double, toLat: Double, toLong: Double, marker: Marker, handler: Handler) {
        val brng = bearingBetweenLocations(fromLat, fromLong, toLat, toLong)

        var rotation = marker.rotation

        if (rotation >= 360) {
            rotation %= 360
        } else if (rotation <= -360) {
            rotation %= 360
            rotation += 360
        }

        var newAngle = marker.rotation.toDouble()
        if (brng - rotation in 0.0..180.0) {
            newAngle = marker.rotation + (brng - rotation)
        } else if (brng - rotation >= 0 && brng - rotation >= 180) {
            newAngle = marker.rotation - (360 - (brng - rotation))
        } else if (brng - rotation <= 0 && brng - rotation >= -180) {
            newAngle = marker.rotation + brng - rotation
        } else if (brng - rotation <= 0 && brng - rotation <= -180) {
            newAngle = marker.rotation + (360 - (rotation - brng))
        }


        val start = SystemClock.uptimeMillis()
        val startRotation = marker.rotation
        val toRotation = newAngle.toFloat()
        val duration: Long = 1500

        val interpolator = LinearInterpolator()
        handler.post(object : MyRunnable(start, interpolator, duration.toFloat(), toRotation, startRotation, marker, handler) {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = interpolator.getInterpolation(elapsed.toFloat() / duration)

                val rot = t * toRotation + (1 - t) * startRotation

                marker.rotation = if (-rot >= 180) rot / 2 else rot
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                }
            }
        })

    }

    private open inner class MyRunnable(var start: Long, var interpolator: Interpolator, var duration: Float, var toRotation: Float, var startRotation: Float, var marker: Marker, var handler: Handler) : Runnable {

        override fun run() {

        }
    }

    private fun bearingBetweenLocations(fromLat: Double, fromLong: Double, toLat: Double, toLong: Double): Double {

        val PI = 3.14159
        val lat1 = fromLat * PI / 180
        val long1 = fromLong * PI / 180
        val lat2 = toLat * PI / 180
        val long2 = toLong * PI / 180

        val dLon = long2 - long1

        val y = Math.sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon)

        var brng = Math.atan2(y, x)

        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360

        return brng
    }

    private interface DistanceCalculated {

        fun sendDistance(distance: Double)
    }

    private inner class DownloadTask : AsyncTask<String, Void, String> {

        constructor(source: LatLng, dest: LatLng, map: GoogleMap) {

            origin = source
            destination = dest
            routeMap = map
        }

        constructor(source: LatLng, dest: LatLng, distanceCalculated: DistanceCalculated) {

            onDistanceCalculated = distanceCalculated
            origin = source
            destination = dest
        }


        override fun doInBackground(vararg url: String): String {
            var data = ""
            try {
                data = downloadUrl(url[0])
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.d("Background Task", e.toString())

                }
            }

            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask = ParserTask()
            parserTask.execute(result)

        }
    }

    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {
        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng>? = null
            var lineOptions = PolylineOptions()
            if (result == null) {
                return
            }
            if (result.isEmpty()) {
                return
            }

            for (i in result.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()
                val path = result[i]
                for (j in path.indices) {
                    val point = path[j]
                    if (j == 0) {
                        val line = point["distance"]
                        if (line != null) {
                            val parts = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            val distance = java.lang.Double.parseDouble(parts[0].replace(",", "."))

                            val dis = ceil(distance).toInt()
                            totalDistance = distance
                            distanceValue = line
                            if (onDistanceCalculated != null) {
                                onDistanceCalculated!!.sendDistance(distance)
                            }
                        }
                        continue

                    } else if (j == 1) {

                        val duration = point["duration"]
                        totalDuration = duration
                        if (duration!!.contains("hours") && (duration.contains("mins") || duration
                                        .contains("min"))) {

                            val arr = duration.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            var timeDur = 0
                            for (k in arr.indices) {
                                when (k) {
                                    0 -> timeDur = Integer.parseInt(arr[k]) * 60
                                }
                                when (k) {
                                    2 -> timeDur += Integer.parseInt(arr[k])
                                }

                            }
                            totalDuration = timeDur.toString()
                        } else if (duration.contains("mins") || duration.contains("min")) {
                            val words = duration.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            totalDuration = words[0]
                        }
                        continue
                    }

                    val lat = java.lang.Double.parseDouble(point["lat"]!!)
                    val lng = java.lang.Double.parseDouble(point["lng"]!!)
                    val position = LatLng(lat, lng)
                    points.add(position)
                }

                lineOptions.addAll(points)
                lineOptions.width(8f)
                lineOptions.color(Color.BLACK)
            }
            polyline?.remove()
            if (value) {
                if (routeMap != null) {
                    routeMap!!.setOnMapLoadedCallback {
                        val builder = LatLngBounds.Builder()
                        builder.include(origin!!)
                        builder.include(destination!!)
                        val bounds = builder.build()
                        val padding = 50
                        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                        routeMap!!.animateCamera(cu)
                    }

                }
            }
            if (routeMap != null && onPolyLineReceived != null) {
                polyline = routeMap!!.addPolyline(lineOptions)
                polyline?.points = points
                onPolyLineReceived!!.onPolyLineReceived(origin, destination, routeMap!!, distanceValue, totalDuration)
            }

        }
    }

    fun setPolyLineReceivedListener(onPolyLineReceived: OnPolyLineReceived) {
        this.onPolyLineReceived = onPolyLineReceived
    }

    interface OnPolyLineReceived {
        fun onPolyLineReceived(origin: LatLng?, destination: LatLng?, routeMap: GoogleMap, totalDistance: String?, totalDuration: String?)
    }

    companion object {
        private var mapUtils: GoogleApisHandle? = null
        private var onDistanceCalculated: DistanceCalculated? = null

        private val MIN_TIME_BW_UPDATES: Long = 0
        private val MIN_DISTANCE_CHANGE_FOR_UPDATES = 0f

        fun getInstance(context: Context): GoogleApisHandle {
            when (mapUtils) {
                null -> mapUtils = GoogleApisHandle()
            }
            mapUtils!!.setAct(context)
            return mapUtils!!
        }
    }
}