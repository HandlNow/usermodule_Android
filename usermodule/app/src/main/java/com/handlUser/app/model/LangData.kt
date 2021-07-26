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
data class LangData(
    @SerializedName("created_by_id")
    val createdById: Int = 0,
    @SerializedName("created_on")
    val createdOn: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("language_id")
    val languageId: Int = 0,
    @SerializedName("language_title")
    val languageTitle: String = "",
    @SerializedName("state_id")
    val stateId: Int = 0,
    @SerializedName("type_id")
    val typeId: Int = 0
) : Parcelable