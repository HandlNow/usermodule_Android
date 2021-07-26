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
import com.google.gson.annotations.Expose

import kotlinx.android.parcel.Parcelize

import com.google.gson.annotations.SerializedName


@SuppressLint("ParcelCreator")
@Parcelize
data class AppointmentData(
        @SerializedName("address")
        val address: String = "",
        @SerializedName("state_id_chat")
        var stateIdChat: String = "",
        @SerializedName("last_message")
        var lastMessge: String = "",
        @SerializedName("bookingSlots")
        val bookingSlots: ArrayList<BookingSlot> = arrayListOf(),
        @SerializedName("createdBy")
        val createdBy: ProfileData = ProfileData(),
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("description")
        val description: String = "",
        @SerializedName("provider_description")
        val providerDescription: String = "",
        @SerializedName("user_description")
        val userDescription: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("latitude")
        val latitude: String = "",
        @SerializedName("longitude")
        val longitude: String = "",
        @SerializedName("price")
        val price: String = "",
        @SerializedName("provider")
        val provider: Provider = Provider(),
        @SerializedName("provider_id")
        val providerId: Int = 0,
        @SerializedName("state_id")
        var stateId: Int = 0,
        @SerializedName("type_id")
        val typeId: Int = 0,
        @SerializedName("user_requirement_id")
        val userRequirementId: Int = 0,
        @SerializedName("payment_status")
        val paymentStatus: Int = 0,
        @SerializedName("userService")
        val userService: UserService = UserService(),
        @SerializedName("user_service_id")
        val userServiceId: Int = 0,
        @SerializedName("distance")
        val distance: String = "",
        @SerializedName("is_rate")
        val isRate: Boolean = false,
        @SerializedName("start_time")
        @Expose
        val startTime: String? = null,

        @SerializedName("end_time")
        @Expose
        val endTime: String? = null,
        @SerializedName("is_booked")
        val isBooked: Boolean = false,
        @SerializedName("rating")
        val rating: NotificationData? = null,
        @SerializedName("selecteLanguage")
        val selecteLanguage: ArrayList<LangData> = arrayListOf()
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class BookingSlot(
        @SerializedName("booking_id")
        val bookingId: Int = 0,
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("provider_id")
        val providerId: Int = 0,
        @SerializedName("slot_id")
        val slotId: Int = 0,
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("type_id")
        val typeId: Int = 0,
        @SerializedName("start_time")
        val startTime: String = "",
        @SerializedName("end_time")
        val endTime: String = ""
) : Parcelable


@SuppressLint("ParcelCreator")
@Parcelize
data class Provider(
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
        @SerializedName("complete_booking")
        val completeBooking: String = "",
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
        @SerializedName("last_visit_time")
        val lastVisitTime: String = "",
        @SerializedName("latitude")
        val latitude: String = "",
        @SerializedName("longitude")
        val longitude: String = "",
        @SerializedName("oathUrl")
        val oathUrl: String = "",
        @SerializedName("otp_verified")
        val otpVerified: Int = 0,
        @SerializedName("profile_file",alternate = ["profile file"])
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
        @SerializedName("total_parkings")
        val totalParkings: String = "",
        @SerializedName("transpotation_km")
        val transpotationKm: String = "",
        @SerializedName("type_id")
        val typeId: Int = 0,
        @SerializedName("zipcode")
        val zipcode: String = ""
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class UserService(
        @SerializedName("category_id")
        val categoryId: Int = 0,
        @SerializedName("category_name")
        val categoryName: String = "",
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("price")
        val price: String = "",
        @SerializedName("remember_me")
        val rememberMe: Int = 0,
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("sub_category_id")
        val subCategoryId: Int = 0,
        @SerializedName("subcategory_name")
        val subcategoryName: String = "",
        @SerializedName("title")
        val title: String = "",
        @SerializedName("type_id")
        val typeId: Int = 0,
        @SerializedName("userSubservices")
        val userSubservices: ArrayList<UserSubservices> = arrayListOf()
) : Parcelable
