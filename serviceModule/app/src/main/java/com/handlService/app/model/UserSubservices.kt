package com.handlService.app.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@SuppressLint("ParcelCreator")
@Parcelize
data class UserSubservices(
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("user_service_id")
        val userServiceId: Int = 0,

        @SerializedName("parent_id")
        val parentId: Int = 0,

        @SerializedName("is_parent_quantity")
        val isParentQuantity: Int = 0,

        @SerializedName("parent_name")
        val parentName: String = "",

        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("type_id")
        val typeId: Int = 0
) : Parcelable
