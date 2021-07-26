/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *
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
data class ChatData(
        @SerializedName("created_on")
        var createdOn: String = "",
        @SerializedName("from_id")
        var fromId: Int = 0,
        @SerializedName("from_user_profile_file")
        var fromUserProfileFile: String = "",
        @SerializedName("group_id")
        var groupId: Int = 0,
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("is_read")
        var isRead: Int = 0,
        @SerializedName("message")
        var message: String = "",
        @SerializedName("readers")
        var readers: String = "",
        @SerializedName("send_on")
        var sendOn: String = "",
        @SerializedName("state_id")
        var stateId: Int = 0,
        @SerializedName("to_id")
        var toId: Int = 0,
        @SerializedName("to_user_profile_file")
        var toUserProfileFile: String = "",
        @SerializedName("type_id")
        var typeId: Int = 0,
        @SerializedName("users")
        var users: String = "",
        @SerializedName("model_id")
        var modelId: Int = 0,
        @SerializedName("model_type")
        var modelType: String = "",
        @SerializedName("to_user_id")
        var toUserId: Int = 0,
        @SerializedName("user_id")
        var userId: Int = 0,
        @SerializedName("created_by_id")
        var createdById: Int = 0
) : Parcelable
