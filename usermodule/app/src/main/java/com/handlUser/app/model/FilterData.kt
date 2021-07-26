/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.model

import android.annotation.SuppressLint
import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

import com.google.gson.annotations.SerializedName


@SuppressLint("ParcelCreator")
@Parcelize
data class FilterData(
        @SerializedName("popular")
        var popular: Int = 0,
        @SerializedName("review")
        var review: Int = 0,
        @SerializedName("nearest")
        var nearest: Int = 0,
        @SerializedName("price_low")
        var priceLow: Int = 0,
        @SerializedName("price_high")
        var priceHigh: Int = 0
) : Parcelable