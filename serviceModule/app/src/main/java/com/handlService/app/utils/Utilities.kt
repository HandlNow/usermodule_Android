/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.utils

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlin.math.sin

object Utilities {
    fun isMarkerVisible(googleMap: GoogleMap, newLocation: LatLng?): Boolean {
        return googleMap.projection.visibleRegion.latLngBounds.contains(newLocation)
    }

    fun bearingBetweenLocations(latLng1: LatLng, latLng2: LatLng): Double {
        val PI = 3.14159
        val lat1 = latLng1.latitude * PI / 180
        val long1 = latLng1.longitude * PI / 180
        val lat2 = latLng2.latitude * PI / 180
        val long2 = latLng2.longitude * PI / 180
        val dLon = long2 - long1
        val y = sin(dLon) * Math.cos(lat2)
        val x = Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon))
        var brng = Math.atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        return brng
    }

    fun computeRotation(fraction: Float, start: Float, end: Float): Float {
        val normalizeEnd = end - start // rotate start to 0
        val normalizedEndAbs = (normalizeEnd + 360) % 360
        val direction: Float =
            if (normalizedEndAbs > 180) -1f else 1.toFloat() // -1 = anticlockwise, 1 = clockwise
        val rotation: Float
        rotation = if (direction > 0) {
            normalizedEndAbs
        } else {
            normalizedEndAbs - 360
        }
        val result = fraction * rotation + start
        return (result + 360) % 360
    }

    interface LatLngInterpolator {
        fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng
        class LinearFixed : LatLngInterpolator {
            override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
                val lat = (b.latitude - a.latitude) * fraction + a.latitude
                var lngDelta = b.longitude - a.longitude
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360
                }
                val lng = lngDelta * fraction + a.longitude
                return LatLng(lat, lng)
            }
        }
    }
}