/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class UserAddressListModel {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("km")
    @Expose
    var km: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: String? = null

    @SerializedName("longitude")
    @Expose
    var longitude: String? = null

    @SerializedName("city")
    @Expose
    var city: String? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("zipcode")
    @Expose
    var zipcode: String? = null

    @SerializedName("state_id")
    @Expose
    var stateId: Int? = null

    @SerializedName("type_id")
    @Expose
    var typeId: Int? = null

    @SerializedName("created_by_id")
    @Expose
    var createdById: Int? = null

}