package com.handlService.app.model

import android.os.Parcelable
import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
class AvailableSlot :Parcelable{
    @SerializedName("id")
    @Expose
     val id: Int? = null

    @SerializedName("day_id")
    @Expose
     val dayId: Int? = null

    @SerializedName("start_time")
    @Expose
     val startTime: String? = null

    @SerializedName("end_time")
    @Expose
     val endTime: String? = null

    @SerializedName("recurring_id")
    @Expose
     val recurringId: String? = null

    @SerializedName("state_id")
    @Expose
     val stateId: Int? = null

    @SerializedName("type_id")
    @Expose
     val typeId: Int? = null

    @SerializedName("created_on")
    @Expose
     val createdOn: String? = null

    @SerializedName("date")
    @Expose
     val date: String? = null

    @SerializedName("created_by_id")
    @Expose
     val createdById: Int? = null

    @SerializedName("availabilitySlots")
    @Expose
     val availabilitySlots: List<AvailabilitySlot>? = null
}