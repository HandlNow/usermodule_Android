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
data class MyServiceData(
        @SerializedName("category_id")
        var categoryId: Int = 0,
        @SerializedName("category_name")
        var categoryName: String = "",
        @SerializedName("created_by_id")
        var createdById: Int = 0,
        @SerializedName("created_on")
        var createdOn: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("price")
        var price: String = "",
        @SerializedName("state_id")
        var stateId: Int = 0,
        @SerializedName("sub_category_id")
        var subCategoryId: Int = 0,
        @SerializedName("sub_category_name")
        var subCategoryName: String = "",
        @SerializedName("sub_category_type")
        var subCategoryType: Int = 0,
        @SerializedName("type_id")
        var typeId: Int = 0
) : Parcelable