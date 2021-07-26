/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.utils

import com.google.android.gms.maps.model.LatLng
import com.handlService.app.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class DirectionsJSONParser {

    fun parse(jObject: JSONObject): List<List<HashMap<String, String>>> {

        val routes = ArrayList<List<HashMap<String, String>>>()
        val jRoutes: JSONArray
        var jLegs: JSONArray
        var jSteps: JSONArray
        var jDistance: JSONObject
        var jDuration: JSONObject

        try {
            jRoutes = jObject.getJSONArray("routes")
            for (i in 0 until jRoutes.length()) {
                jLegs = (jRoutes.get(i) as JSONObject).getJSONArray("legs")
                val path = ArrayList<HashMap<String, String>>()
                for (j in 0 until jLegs.length()) {

                    jDistance = (jLegs.get(j) as JSONObject).getJSONObject("distance")
                    val hmDistance = HashMap<String, String>()
                    hmDistance["distance"] = jDistance.getString("text")
                    jDuration = (jLegs.get(j) as JSONObject).getJSONObject("duration")
                    val hmDuration = HashMap<String, String>()
                    hmDuration["duration"] = jDuration.getString("text")
                    path.add(hmDistance)
                    path.add(hmDuration)

                    jSteps = (jLegs.get(j) as JSONObject).getJSONArray("steps")

                    for (k in 0 until jSteps.length()) {
                        val polyline: String = ((jSteps
                                .get(k) as JSONObject).get("polyline") as JSONObject).get("points") as String
                        val list = decodePoly(polyline)


                        for (l in list.indices) {
                            val hm = HashMap<String, String>()
                            hm["lat"] = java.lang.Double.toString(list[l].latitude)
                            hm["lng"] = java.lang.Double.toString(list[l].longitude)
                            path.add(hm)
                        }
                    }
                }
                routes.add(path)
            }

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        return routes
    }


    private fun decodePoly(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }
}