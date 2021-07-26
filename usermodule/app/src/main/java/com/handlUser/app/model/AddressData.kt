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
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class AddressData(

        @SerializedName("title")
        var title: String? = "",
        @SerializedName("address")
        var address: String? = "",
        @SerializedName("subthorough")
        var subthorough: String? = "",
        @SerializedName("thorough")
        var thorough: String? = "",
        @SerializedName("id")
        var id: Int? = 0,

        @SerializedName("longitude")
        var longitude: String? = "",
        @SerializedName("latitude")
        var latitude: String? = "",
        @SerializedName("city")
        var city: String? = "",
        @SerializedName("state")
        var state: String? = "",
        @SerializedName("zipcode")
        var zipcode: String? = "",
        @SerializedName("country")
        var country: String? = "",
        @SerializedName("first_name", alternate = ["name"])
        var firstName: String? = "",
        @SerializedName("last_name")
        var lastName: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("contact_no", alternate = ["number"])
        var contactNo: String? = "",

        var apartmentAddress: String? = "",
        var isZoomMap: Boolean = false,
        var isSelected: Boolean = false


) : Parcelable