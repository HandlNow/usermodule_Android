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
data class ProfileData(
        @SerializedName("referral_code")
        var referralCode: String = "",
        @SerializedName("about_me")
        var aboutMe: String = "",
        @SerializedName("complete_booking")
        var completeBooking: String = "",
        @SerializedName("total_parkings")
        val totalParkings: String = "",
        @SerializedName("add_bank_webview_url", alternate = ["oathUrl"])
        var addBankWebViewUrl: String = "",
        @SerializedName("profile_file")
        var profileFile: String = "",
        @SerializedName("access_token")
        var accessToken: String = "",
        @SerializedName("address")
        var address: String = "",
        @SerializedName("city")
        var city: String = "",
        @SerializedName("contact_no")
        var contactNo: String = "",
        @SerializedName("country")
        var country: String = "",
        @SerializedName("country_code")
        var countryCode: String = "",
        @SerializedName("created_by_id")
        var createdById: Int = 0,
        @SerializedName("notification_count")
        var notificationCount: Int = 0,
        @SerializedName("created_by_id_chat")
        var createdByIdChat: Int = 0,
        @SerializedName("created_on")
        var createdOn: String = "",
        @SerializedName("created_on_chat")
        var createdOnChat: String = "",
        @SerializedName("customer_id")
        var customerId: String = "",
        @SerializedName("date_of_birth")
        var dateOfBirth: String = "",
        @SerializedName("email")
        var email: String = "",
        @SerializedName("first_name")
        var firstName: String = "",
        @SerializedName("full_name")
        var fullName: String = "",
        @SerializedName("last_message")
        var lastMessge: String = "",
        @SerializedName("message_time")
        var messageTime: String = "",
        @SerializedName("gender")
        var gender: Int = 0,
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("id_proof")
        var idProof: String = "",
        @SerializedName("rating")
        var rating: Float = 0f,
        @SerializedName("is_image_verify")
        var isImageVerify: Int = 0,
        @SerializedName("is_proof_verify")
        var isProofVerify: Int = 0,
        @SerializedName("is_transpotation")
        var isTranspotation: Int = 0,
        @SerializedName("is_verified")
        var isVerified: Int = 0,
        @SerializedName("language")
        var language: String = "",
        @SerializedName("last_action_time")
        var lastActionTime: String = "",
        @SerializedName("last_name")
        var lastName: String = "",
        @SerializedName("last_visit_time")
        var lastVisitTime: String = "",
        @SerializedName("latitude")
        var latitude: String = "",
        @SerializedName("login_error_count")
        var loginErrorCount: String = "",
        @SerializedName("longitude")
        var longitude: String = "",
        @SerializedName("otp")
        var otp: Int = 0,
        @SerializedName("otp_verified")
        var otpVerified: Int = 0,
        @SerializedName("role_id")
        var roleId: Int = 0,
        @SerializedName("state_id")
        var stateId: Int = 0,
        @SerializedName("state_id_chat")
        var stateIdChat: Int = 0,
        @SerializedName("timezone")
        var timezone: String = "",
        @SerializedName("tos")
        var tos: String = "",
        @SerializedName("profile_file_chat")
        var profileFileChat: String = "",
        @SerializedName("transpotation_km")
        var transpotationKm: String = "",
        @SerializedName("type_id")
        var typeId: Int = 0,
        @SerializedName("type_id_chat")
        var typeIdChat: Int = 0,
        @SerializedName("full_name_chat")
        var fullNameChat: String? = "",
        @SerializedName("is_subscription")
        var isSubscription: Boolean = false,
        @SerializedName("is_location")
        var isLocation: Boolean = false,
        @SerializedName("is_service")
        var isService: Boolean = false,
        @SerializedName("is_available")
        var isAvailable: Boolean = false,
        @SerializedName("is_account")
        var isAccount: Boolean = false,
        @SerializedName("is_language")
        var isLanguage: Boolean = false,
        @SerializedName("zipcode")
        var zipcode: String = "",
        @SerializedName("account_detail")
        var accountDetail: ArrayList<AccountData> = arrayListOf()
) : Parcelable