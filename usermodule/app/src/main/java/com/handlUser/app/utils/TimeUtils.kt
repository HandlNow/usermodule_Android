/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.utils


object TimeUtils {

    val ONE_SECOND: Long = 1000
    val SECONDS: Long = 60

    val ONE_MINUTE = ONE_SECOND * 60
    val MINUTES: Long = 60

    val ONE_HOUR = ONE_MINUTE * 60
    val HOURS: Long = 24

    val ONE_DAY = ONE_HOUR * 24
    val ONE_MONTH = ONE_DAY * 30
    val ONE_YEAR = ONE_MONTH * 365

    enum class DIFFERNECE_FACTOR {
        SEC, MIN, HOUR, DAY, MONTH, YEAR
    }

    /**
     * converts time (in milliseconds) to human-readable format
     * "<w> days, <x> hours, <y> minutes and (z) seconds"
    </y></x></w> */
    fun millisToLongDHMS(duration: Long): String {
        var duration = duration
        val res = StringBuffer()
        var temp: Long = 0
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY
            if (temp > 0) {
                duration -= temp * ONE_DAY
                res.append(temp).append(" day").append(if (temp > 1) "s" else "")
                        .append(if (duration >= ONE_MINUTE) ", " else "")
            }

            temp = duration / ONE_HOUR
            if (temp > 0) {
                duration -= temp * ONE_HOUR
                res.append(temp).append(" hour").append(if (temp > 1) "s" else "")
                        .append(if (duration >= ONE_MINUTE) ", " else "")
            }

            temp = duration / ONE_MINUTE
            if (temp > 0) {
                duration -= temp * ONE_MINUTE
                res.append(temp).append(" min").append(if (temp > 1) "s" else "")
            }

            if (res.toString() != "" && duration >= ONE_SECOND) {
                res.append(" and ")
            }

            temp = duration / ONE_SECOND
            if (temp > 0) {
                res.append(temp).append(" s")
            }
            return res.toString()
        } else {
            return "0 second"
        }
    }

    fun checkDateDiff(timeInMillis: Long, compareWithTimeInMillis: Long, differenceFactor: DIFFERNECE_FACTOR, differenceValue: Int): Boolean {
        var difference: Long = 0
        when (differenceFactor) {
            TimeUtils.DIFFERNECE_FACTOR.SEC -> difference = ONE_SECOND * differenceValue
            TimeUtils.DIFFERNECE_FACTOR.MIN -> difference = ONE_MINUTE * differenceValue
            TimeUtils.DIFFERNECE_FACTOR.HOUR -> difference = ONE_HOUR * differenceValue
            TimeUtils.DIFFERNECE_FACTOR.DAY -> difference = ONE_DAY * differenceValue
            TimeUtils.DIFFERNECE_FACTOR.MONTH -> difference = ONE_MONTH * differenceValue
            TimeUtils.DIFFERNECE_FACTOR.YEAR -> difference = ONE_YEAR * differenceValue
        }
        return compareWithTimeInMillis - timeInMillis > difference
    }

    fun getShortDHMS(duration: Long): String {

        return if (duration < ONE_MINUTE)
            (duration / ONE_SECOND).toString() + " s"
        else if (duration < ONE_HOUR)
            (duration / ONE_MINUTE).toString() + " min"
        else if (duration < ONE_DAY)
            (duration / ONE_HOUR).toString() + " hour"
        else
            (duration / ONE_DAY).toString() + " day"
    }

    fun getAgoTime(timeInMillis: Long): String {
        return getShortDHMS(System.currentTimeMillis() - timeInMillis) + " ago"
    }

    fun formatSeconds(timeInSeconds: Int): String? {
        val secondsLeft = timeInSeconds
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60
        var formattedTime = ""

        if (minutes < 10) formattedTime += "0"
        formattedTime += "$minutes:"
        if (seconds < 10) formattedTime += "0"
        formattedTime += seconds
        return formattedTime
    }

}

