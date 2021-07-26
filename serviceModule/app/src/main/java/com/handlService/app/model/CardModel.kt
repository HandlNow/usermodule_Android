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
data class CardModel(
        @SerializedName("address_city")
        var addressCity: String = "",
        @SerializedName("address_country")
        var addressCountry: String = "",
        @SerializedName("address_line1")
        var addressLine1: String = "",
        @SerializedName("address_line1_check")
        var addressLine1Check: String = "",
        @SerializedName("address_line2")
        var addressLine2: String = "",
        @SerializedName("address_state")
        var addressState: String = "",
        @SerializedName("address_zip")
        var addressZip: String = "",
        @SerializedName("address_zip_check")
        var addressZipCheck: String = "",
        @SerializedName("brand")
        var brand: String = "",
        @SerializedName("country")
        var country: String = "",
        @SerializedName("customer")
        var customer: String = "",
        @SerializedName("cvc_check")
        var cvcCheck: String = "",
        @SerializedName("dynamic_last4")
        var dynamicLast4: String = "",
        @SerializedName("exp_month")
        var expMonth: Int = 0,
        @SerializedName("exp_year")
        var expYear: Int = 0,
        @SerializedName("fingerprint")
        var fingerprint: String = "",
        @SerializedName("funding")
        var funding: String = "",
        @SerializedName("id")
        var id: String = "",
        @SerializedName("last4")
        var last4: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("object")
        var objectX: String = "",
        @SerializedName("tokenization_method")
        var tokenizationMethod: String = "",
        var isSelected: Boolean = false
) : Parcelable