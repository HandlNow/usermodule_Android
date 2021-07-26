/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.model

import java.util.Calendar

sealed class CalendarEntity {

    data class Event(
        val id: Long,
        val title: CharSequence,
        val startTime: Calendar,
        val endTime: Calendar,
        val location: CharSequence,
        val color: Int,
        val isAllDay: Boolean,
        val isCanceled: Boolean
    ) : CalendarEntity()

}
