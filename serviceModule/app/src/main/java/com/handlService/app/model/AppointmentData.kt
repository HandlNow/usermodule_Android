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
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@SuppressLint("ParcelCreator")
@Parcelize
data class AppointmentData(
        var monthYear: String = "",
        @SerializedName("address")
        val address: String = "",
        @SerializedName("state_id_chat")
        var stateIdChat: String = "",
        @SerializedName("provider_description")
        val providerDescription: String = "",
        @SerializedName("user_description")
        val userDescription: String = "",
        @SerializedName("last_message")
        var lastMessge: String = "",
        @SerializedName("bookingSlots", alternate = ["availabilitySlots"])
        val bookingSlots: ArrayList<BookingSlot> = arrayListOf(),
        @SerializedName("createdBy")
        val createdBy: ProfileData = ProfileData(),
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("description")
        val description: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("slot_id")
        var slotId: SlotData? = null,
        @SerializedName("latitude")
        val latitude: String = "",
        @SerializedName("longitude")
        val longitude: String = "",
        @SerializedName("price")
        val price: String = "",
        @SerializedName("provider")
        val provider: ProviderData = ProviderData(),
        @SerializedName("provider_id")
        val providerId: Int = 0,
        @SerializedName("state_id")
        var stateId: Int = 0,
        @SerializedName("type_id")
        val typeId: Int = 0,
        @SerializedName("user_requirement_id")
        val userRequirementId: Int = 0,
        @SerializedName("userService")
        val userService: UserService = UserService(),
        @SerializedName("user_service_id")
        val userServiceId: Int = 0,
        var isRequest: Int = 0,
        @SerializedName("is_rate")
        val isRate: Boolean = false,
        @SerializedName("is_booked")
        val isBooked: Boolean = false,
        @SerializedName("rating")
        val rating: NotificationData? = null,

        @SerializedName("start_time")
        @Expose
        val startTime: String? = null,

        @SerializedName("end_time")
        @Expose
        val endTime: String? = null,

        @SerializedName("availability_id")
        @Expose
        val availabilityId: Int? = null,

        @SerializedName("booking_id")
        @Expose
        val bookingId: Int? = null,
        @SerializedName("booking_detail")
        @Expose
        val bookingDetail: AppointmentData? = null,
        @SerializedName("user_detail")
        @Expose
        val userDetail: ProfileData? = null,

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
        val endTime: String = "",
        @SerializedName("bookingDetail")
        val bookingDetail: AppointmentData? = null,
        @SerializedName("is_booked")
        val isBooked: Boolean = false
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

