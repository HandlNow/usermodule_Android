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
data class NotificationData(
        @SerializedName("created_by_id")
        var createdById: Int = 0,
        @SerializedName("action")
        var action: String = "",
        @SerializedName("controller")
        var controller: String = "",
        @SerializedName("full_name")
        var fullName: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("created_on")
        var createdOn: String = "",
        @SerializedName("created")
        var created: String = "",
        @SerializedName("rating")
        var rating: String = "",
        @SerializedName("description", alternate = ["answer"])
        var description: String = "",
        @SerializedName("amount")
        var amount: String = "",
        @SerializedName("from_name")
        var fromName: String = "",
        @SerializedName("user_name")
        var userName: String = "",
        @SerializedName("user_image")
        var userImage: String = "",
        @SerializedName("from_profile_file")
        var fromProfileFile: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("is_read")
        var isRead: Int = 0,
        @SerializedName("model_id")
        var modelId: Int = 0,
        @SerializedName("model_type")
        var modelType: String = "",
        @SerializedName("role")
        var role: Int = 0,
        @SerializedName("send_on")
        var sendOn: String = "",
        @SerializedName("state_id")
        var stateId: Int = 0,
        @SerializedName("language_id")
        var languageId: Int = 0,
        @SerializedName("title", alternate = ["question", "language_title"])
        var title: String = "",
        @SerializedName("message")
        var message: String = "",
        @SerializedName("comment")
        var comment: String = "",
        @SerializedName("to_user_id")
        var toUserId: Int = 0,
        @SerializedName("user_id")
        var userId: Int = 0,
        @SerializedName("type_id")
        var typeId: Int = 0,
        @SerializedName("is_check")
        var isChecked: Boolean = true

) : Parcelable