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
data class ProviderFile(
    @SerializedName("created_by_id")
    val createdById: Int = 0,
    @SerializedName("created_on")
    val createdOn: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("key")
    val key: String = "",
    @SerializedName("model_id")
    val modelId: Int = 0,
    @SerializedName("model_type")
    val modelType: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("size")
    val size: Int = 0,
    @SerializedName("type_id")
    val typeId: Int = 0
) : Parcelable