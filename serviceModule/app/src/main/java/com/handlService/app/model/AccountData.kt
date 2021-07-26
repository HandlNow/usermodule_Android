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

import kotlinx.android.parcel.Parcelize

import com.google.gson.annotations.SerializedName


@SuppressLint("ParcelCreator")
@Parcelize
data class AccountData(
        @SerializedName("account")
        val account: String = "",
        @SerializedName("account_holder_name")
        val accountHolderName: String = "",
        @SerializedName("account_holder_type")
        val accountHolderType: String = "",
        @SerializedName("available_payout_methods")
        val availablePayoutMethods: ArrayList<String> = arrayListOf(),
        @SerializedName("bank_name")
        val bankName: String = "",
        @SerializedName("country")
        val country: String = "",
        @SerializedName("currency")
        val currency: String = "",
        @SerializedName("default_for_currency")
        val defaultForCurrency: Boolean = false,
        @SerializedName("fingerprint")
        val fingerprint: String = "",
        @SerializedName("id")
        val id: String = "",
        @SerializedName("last4")
        val last4: String = "",
        @SerializedName("metadata")
        val metadata: ArrayList<String> = arrayListOf(),
        @SerializedName("object")
        val objectX: String = "",
        @SerializedName("routing_number")
        val routingNumber: String = "",
        @SerializedName("status")
        val status: String = ""
) : Parcelable