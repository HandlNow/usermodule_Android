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

@Parcelize
data class ProfileData(
        @SerializedName("total_parkings")
        var totalParkings: String = "",
        @SerializedName("referral_code")
        var referralCode: String = "",
        @SerializedName("is_user_address")
        var isUserAddress: Boolean = false,
        @SerializedName("created_on_chat")
        var createdOnChat: String = "",
        @SerializedName("state_id_chat")
        var stateIdChat: Int = 0,
        @SerializedName("type_id_chat")
        var typeIdChat: Int = 0,
        @SerializedName("created_by_id_chat")
        var createdByIdChat: Int = 0,
        @SerializedName("is_user_payment")
        var isUserPayment: Boolean = false,
        @SerializedName("access_token")
        var accessToken: String? = "",
        @SerializedName("country_code")
        var countryCode: String? = "",
        @SerializedName("about_me")
        var aboutMe: String? = "",
        @SerializedName("notification_count")
        var notificationCount: Int = 0,
        @SerializedName("address")
        var address: String? = "",
        @SerializedName("city")
        var city: String? = "",
        @SerializedName("contact_no")
        var contactNo: String? = "",
        @SerializedName("country")
        var country: String? = "",
        @SerializedName("created_by_id")
        var createdById: String? = "",
        @SerializedName("created_on")
        var createdOn: String? = "",
        @SerializedName("date_of_birth")
        var dateOfBirth: String? = "",
        @SerializedName("email")
        var email: String? = "",
        @SerializedName("pass")
        var pass: String? = "",
        @SerializedName("first_name")
        var firstName: String? = "",
        @SerializedName("full_name")
        var fullName: String? = "",
        @SerializedName("full_name_chat")
        var fullNameChat: String? = "",
        @SerializedName("profile_file")
        var profileFile: String? = "",
        @SerializedName("profile_file_chat")
        var profileFileChat: String = "",
        @SerializedName("gender")
        var gender: Int? = 0,
        @SerializedName("id")
        var id: Int? = 0,
        @SerializedName("rating")
        var rating: String = "",
        @SerializedName("last_message")
        var lastMessge: String = "",
        @SerializedName("message_time")
        var messageTime: String = "",
        @SerializedName("language")
        var language: String? = "",
        @SerializedName("last_action_time")
        var lastActionTime: String? = "",
        @SerializedName("last_name")
        var lastName: String? = "",
        @SerializedName("last_visit_time")
        var lastVisitTime: String? = "",
        @SerializedName("latitude")
        var latitude: String? = "",
        @SerializedName("longitude")
        var longitude: String? = "",
        @SerializedName("otp")
        var otp: Int? = 0,
        @SerializedName("otp_verified")
        var otpVerified: Int? = 0,
        @SerializedName("role_id")
        var roleId: Int? = 0,
        @SerializedName("state_id")
        var stateId: Int? = 0,
        @SerializedName("timezone")
        var timezone: String? = "",
        @SerializedName("tos")
        var tos: String? = "",
        @SerializedName("type_id")
        var typeId: Int? = 0,
        @SerializedName("zipcode")
        var zipcode: String? = ""
) : Parcelable