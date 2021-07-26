/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
data class SlotData(
        @SerializedName("created_by_id")
        var createdById: Int = 0,
        @SerializedName("created_on")
        var createdOn: String = "",
        @SerializedName("day_id")
        var dayId: String = "",
        @SerializedName("end_time")
        var endTime: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("recurring_id")
        var recurringId: String = "",
        @SerializedName("start_time")
        var startTime: String = "",
        @SerializedName("state_id")
        var stateId: Int = 0,
        @SerializedName("type_id")
        var typeId: Int = 0,
        var day: String? = null,
        var total_time: String? = null,
        var isFromServer: Boolean = false
) : Parcelable