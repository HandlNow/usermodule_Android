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


@SuppressLint("ParcelCreator")
@Parcelize
data class ServiceData(
        @SerializedName("category_id")
        val categoryId: Int = 0,
        @SerializedName("category_name")
        val categoryName: String = "",
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("remember_me")
        val rememberMe: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("image")
        val image: String = "",
        @SerializedName("is_checked")
        var isChecked: Boolean = false,
        @SerializedName("parent")
        val parent: ArrayList<ParentData> = arrayListOf(),
        @SerializedName("kid")
        val parentKid: ArrayList<ParentData> = arrayListOf(),
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("title")
        val title: String = "",
        @SerializedName("type_id")
        val typeId: Int = 0
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ParentData(
        @SerializedName("category_id")
        val categoryId: Int = 0,
        @SerializedName("children")
        val children: ArrayList<ChildrenData> = arrayListOf(),
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("sub_category_id")
        val subCategoryId: Int = 0,
        @SerializedName("title")
        val title: String = "",
        @SerializedName("value", alternate = ["is_parent_quantity"])
        var value: Int = 0,
        @SerializedName("is_parent_check")
        var isCheck: Int = 0,
        var isChecked: Boolean = false
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class ChildrenData(
        @SerializedName("category_id")
        val categoryId: Int = 0,
        @SerializedName("created_by_id")
        val createdById: Int = 0,
        @SerializedName("created_on")
        val createdOn: String = "",
        @SerializedName("id")
        val id: Int = 0,
        @SerializedName("parent_id")
        val parentId: Int = 0,
        @SerializedName("state_id")
        val stateId: Int = 0,
        @SerializedName("sub_category_id")
        val subCategoryId: Int = 0,
        @SerializedName("title")
        val title: String = "",
        @SerializedName("type_id")
        val typeId: Int = 0,
        @SerializedName("value", alternate = ["is_parent_quantity"])
        var value: Int = 0,
        @SerializedName("is_parent_check")
        var isCheck: Int = 0,
        var isChecked: Boolean = false
) : Parcelable