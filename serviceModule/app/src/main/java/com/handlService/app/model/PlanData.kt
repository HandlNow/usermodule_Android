/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class PlanData(
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("description")
        val description: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("price")
        val price: String = "",
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("title")
        val title: String = "",
        @SerializedName("type_id")
        val typeId: Int = 0,
        @SerializedName("validity")
        val validity: Int = 0,
        @SerializedName("is_buy")
        var isBuy: Boolean = false
) : Parcelable

