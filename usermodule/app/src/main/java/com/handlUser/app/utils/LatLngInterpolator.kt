/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.utils


import com.google.android.gms.maps.model.LatLng
import java.lang.Math.*

interface LatLngInterpolator {
    fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng

    class Linear : LatLngInterpolator {
        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            val lat = (b.latitude - a.latitude) * fraction + a.latitude
            val lng = (b.longitude - a.longitude) * fraction + a.longitude
            return LatLng(lat, lng)
        }
    }

    class LinearFixed : LatLngInterpolator {
        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            val lat = (b.latitude - a.latitude) * fraction + a.latitude
            var lngDelta = b.longitude - a.longitude             // Take the shortest path across the 180th meridian.
            if (Math.abs(lngDelta) > 180) {
                lngDelta -= Math.signum(lngDelta) * 360
            }
            val lng = lngDelta * fraction + a.longitude
            return LatLng(lat, lng)
        }
    }

    class Spherical : LatLngInterpolator {         /* From github.com/googlemaps/android-maps-utils */
        override fun interpolate(fraction: Float, from: LatLng, to: LatLng): LatLng {             // http://en.wikipedia.org/wiki/Slerp
            val fromLat = toRadians(from.latitude)
            val fromLng = toRadians(from.longitude)
            val toLat = toRadians(to.latitude)
            val toLng = toRadians(to.longitude)
            val cosFromLat = cos(fromLat)
            val cosToLat = cos(toLat)             // Computes Spherical interpolation coefficients
            val angle = computeAngleBetween(fromLat, fromLng, toLat, toLng)
            val sinAngle = sin(angle)
            if (sinAngle < 1E-6) {
                return from
            }
            val a = sin((1 - fraction) * angle) / sinAngle
            val b = sin(fraction * angle) / sinAngle
            // Converts from polar to vector and interpolate.
            val x = a * cosFromLat * cos(fromLng) + b * cosToLat * cos(toLng)
            val y = a * cosFromLat * sin(fromLng) + b * cosToLat * sin(toLng)
            val z = a * sin(fromLat) + b * sin(toLat)             // Converts interpolated vector back to polar.
            val lat = atan2(z, sqrt(x * x + y * y))
            val lng = atan2(y, x)
            return LatLng(toDegrees(lat), toDegrees(lng))
        }

        private fun computeAngleBetween(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double): Double {
            // Haversine's formula
            val dLat = fromLat - toLat
            val dLng = fromLng - toLng
            return 2 * asin(sqrt(pow(sin(dLat / 2), 2.0) + cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2.0)))
        }
    }

}
