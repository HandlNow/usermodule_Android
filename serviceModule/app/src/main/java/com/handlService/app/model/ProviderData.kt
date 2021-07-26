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
data class ProviderData(
        @SerializedName("about_me")
        val aboutMe: String = "",
        @SerializedName("access_token")
        val accessToken: String = "",
        @SerializedName("address")
        val address: String = "",
        @SerializedName("avalibility")
        val avalibility: ArrayList<Avalibility> = arrayListOf(),
        @SerializedName("city")
        val city: String = "",
        @SerializedName("contact_no")
        val contactNo: String = "",
        @SerializedName("country")
        val country: String = "",
        @SerializedName("country_code")
        val countryCode: Int = 0,
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("customer_id")
        val customerId: String = "",
        @SerializedName("date_of_birth")
        val dateOfBirth: String = "",
        @SerializedName("email")
        val email: String = "",
        @SerializedName("first_name")
        val firstName: String = "",
        @SerializedName("full_name")
        val fullName: String = "",
        @SerializedName("gender")
        val gender: Int = 0,
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("id_proof")
        val idProof: String = "",
        @SerializedName("is_account")
        val isAccount: Boolean = false,
        @SerializedName("is_available")
        val isAvailable: Boolean = false,
        @SerializedName("is_image_verify")
        val isImageVerify: Int = 0,
        @SerializedName("is_location")
        val isLocation: Boolean = false,
        @SerializedName("is_proof_verify")
        val isProofVerify: Int = 0,
        @SerializedName("is_service")
        val isService: Boolean = false,
        @SerializedName("is_subscription")
        val isSubscription: Boolean = false,
        @SerializedName("is_transpotation")
        val isTranspotation: Int = 0,
        @SerializedName("is_user_address")
        val isUserAddress: Boolean = false,
        @SerializedName("is_user_payment")
        val isUserPayment: Boolean = false,
        @SerializedName("is_verified")
        val isVerified: Int = 0,
        @SerializedName("language")
        val language: String = "",
        @SerializedName("last_action_time")
        val lastActionTime: String = "",
        @SerializedName("last_name")
        val lastName: String = "",
        @SerializedName("last_password_change")
        val lastPasswordChange: String = "",
        @SerializedName("last_visit_time")
        val lastVisitTime: String = "",
        @SerializedName("latitude")
        val latitude: String = "",
        @SerializedName("login_error_count")
        val loginErrorCount: Int = 0,
        @SerializedName("longitude")
        val longitude: String = "",
        @SerializedName("oathUrl")
        val oathUrl: String = "",
        @SerializedName("otp")
        val otp: Int = 0,
        @SerializedName("otp_verified")
        val otpVerified: Int = 0,
        @SerializedName("profile_file")
        val profileFile: String = "",
        @SerializedName("providerService")
        val providerService: ArrayList<ProviderService> = arrayListOf(),
        @SerializedName("referral_code")
        val referralCode: String = "",
        @SerializedName("role_id")
        val roleId: Int = 0,
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("timezone")
        val timezone: String = "",
        @SerializedName("tos")
        val tos: Int = 0,
        @SerializedName("transpotation_km")
        val transpotationKm: String = "",
        @SerializedName("type_id")
        val typeId: Int = 0,
        @SerializedName("zipcode")
        val zipcode: String = "",
        @SerializedName("total_parkings")
        val totalParkings: String = "",
        @SerializedName("complete_booking")
        val completeBooking: String = ""
) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
data class Avalibility(
        @SerializedName("availabilitySlots")
        val availabilitySlots: ArrayList<AvailabilitySlot> = arrayListOf(),
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("day_id")
        val dayId: Int = 0,
        @SerializedName("end_time")
        val endTime: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("recurring_id")
        val recurringId: String = "",
        @SerializedName("start_time")
        val startTime: String = "",
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("type_id")
        val typeId: Int = 0,
        var isChecked: Boolean = false
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ProviderService(
        @SerializedName("category_id")
        val categoryId: Int = 0,
        @SerializedName("category_name")
        val categoryName: String = "",
        @SerializedName("category_type")
        val categoryType: Int = 0,
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("price")
        val price: String = "",
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("sub_category_id")
        val subCategoryId: Int = 0,
        @SerializedName("sub_category_name")
        val subCategoryName: String = "",
        @SerializedName("sub_category_type")
        val subCategoryType: Int = 0,
        @SerializedName("type_id")
        val typeId: Int = 0
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class AvailabilitySlot(
        @SerializedName("availability_id")
        val availabilityId: Int = 0,
        @SerializedName("booking_id")
        val bookingId: Int = 0,
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("states")
        val states: String = "",
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("start_time")
        val startTime: String = "",
        @SerializedName("end_time")
        val endTime: String = "",
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("type_id")
        val typeId: Int = 0,
        var isChecked: Boolean = false

) : Parcelable

