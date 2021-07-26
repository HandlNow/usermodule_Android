/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class CategoryData(
        @SerializedName("created_by_id")
        var createdById: Int = 0,
        @SerializedName("created_on")
        var createdOn: String = "",
        @SerializedName("description")
        var description: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("state_id")
        var stateId: Int = 0,
        @SerializedName("subCategories")
        var subCategories: ArrayList<SubCategory> = arrayListOf(),
        @SerializedName("title")
        var title: String = "",
        @SerializedName("type_id")
        var typeId: Int = 0,
        var isChecked: Boolean = false
) : Parcelable

@Parcelize
data class SubCategory(
        @SerializedName("category_id")
        var categoryId: Int = 0,
        @SerializedName("category_name")
        var categoryName: String = "",
        @SerializedName("created_by_id")
        var createdById: Int = 0,
        @SerializedName("created_on")
        var createdOn: String = "",
        @SerializedName("description")
        var description: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image")
        var image: String = "",
        @SerializedName("state_id")
        var stateId: Int = 0,
        @SerializedName("title")
        var title: String = "",
        @SerializedName("type_id")
        var typeId: Int = 0,
        @SerializedName("is_checked")
        var isCheck: Boolean = false
) : Parcelable