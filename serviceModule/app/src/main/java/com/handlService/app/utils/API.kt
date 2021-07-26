/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.utils

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*

interface API {


    @FormUrlEncoded
    @POST(Const.API_SIGN_UP)
    fun apiSignUp(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @POST(Const.API_DOCUMENT)
    fun apiupdateDocumentation(@Body serverHashMap: RequestBody): Call<String>

    @FormUrlEncoded
    @POST(Const.API_CHECK)
    fun apiCheck(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_CONTACT_US)
    fun apiHelpContactUs(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_CHANGE_PASSWORD)
    fun apiChangePassword(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_LOGIN)
    fun apiLogin(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.API_LOGOUT)
    fun apiLogout(): Call<String>

    @FormUrlEncoded
    @POST(Const.API_RESEND_OTP)
    fun apiOtpResend(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_VERIFY_OTP)
    fun apiVerifyOtp(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_FORGOT)
    fun apiForgot(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.API_STATIC_PAGE)
    fun apiGetStaticPage(@Query("type") type: Int): Call<String>



    @GET(Const.API_BOOKING_AVAILABILITY_LIST)
    fun apiGetDayList(@Query("date") month: String): Call<String>

    // category list
    @GET(Const.API_CATEGORY_LIST)
    fun apiGetCategoryList(@Query("page") page: Int): Call<String>

    @GET(Const.API_TRANSPORTATION_LIST)
    fun apiGetTransportationList(@Query("page") page: Int): Call<String>

    @DELETE(Const.API_TRANSPORTATION_DELETE)
    fun apiTransportationDelete(@Query("id") id: Int): Call<String>

    @FormUrlEncoded
    @POST(Const.API_TRANSPORTATION_ADD)
    fun apiAddTransportation(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.API_MY_CATEGORY_LIST)
    fun apiGetMyCategoryList(@Query("page") page: Int): Call<String>

    @GET(Const.API_MY_CATEGORY_ON_OFF)
    fun apiGetMyCategoryOnOff(@Query("id") id: Int): Call<String>

    @FormUrlEncoded
    @POST(Const.API_CATEGORY_GET_PRICE)
    fun apiGetCategoryPrice(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_CATEGORY_ADD_PRICE)
    fun apiAddCategory(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_CATEGORY_UPDATE_PRICE)
    fun apiUpdateCategoryPrice(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.API_PLAN_LIST)
    fun apiGetPlanList(): Call<String>

    @FormUrlEncoded
    @POST(Const.API_PLAN_BUY)
    fun apiBuyPlan(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    //transaction api

    @FormUrlEncoded
    @POST(Const.API_CARD_ADD)
    fun apiAddCard(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @DELETE(Const.API_CARD_DELETE)
    fun apiCardDelete(@Query("card_id") id: String): Call<String>

    @GET(Const.API_CARD_LIST)
    fun apiCardList(): Call<String>

    //address api
    @FormUrlEncoded
    @POST(Const.API_ADD_ADDRESS)
    fun apiAddAddress(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.API_ADDRESS_LIST)
    fun apiAddressList(): Call<String>

    @DELETE(Const.API_DELETE_ADDRESS)
    fun apiDeleteAddress(@Query("id") id: Int): Call<String>

    @POST(Const.API_UPDATE_PROFILE)
    fun apiUpdateProfile(@Body requestBody: RequestBody): Call<String>


    /*Chat Regarding apis*/

    @POST(Const.API_SEND_MESSAGE)
    fun apiSendMessage(@Body body: RequestBody): Call<String>

    @GET(Const.API_OLD_MESSAGES)
    fun apiGetOldMessages(@Query("user_id") to_user: String? = null, @Query("page") page: Int? = null, @Query("model_id") model_id: String? = null): Call<String>

    @GET(Const.API_INBOX_LIST)
    fun apiGetInboxMessages(@Query("page") page: Int): Call<String>

    @GET(Const.API_NEW_MESSAGES)
    fun apiGetNewMessages(@Query("to_id") to_id: String?): Call<String>

    @GET(Const.API_NOTIFICATION_LIST)
    fun apiGetNotification(@Query("page") page: Int): Call<String>


    @FormUrlEncoded
    @POST(Const.API_ADD_BANK)
    fun apiBank(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>


    //slot api

    @FormUrlEncoded
    @POST(Const.API_AVAIL_GET_SLOT)
    fun hitGetSlotAvailabilities(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>


    @FormUrlEncoded
    @POST(Const.API_AVAIL_ADD_SLOT)
    fun hitAddSlotAvailabilities(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>


    @DELETE(Const.API_AVAIL_DELETE_SLOT)
    fun hitDeleteSlotAvailabilities(@Query("id") id: Int, @Query("type") booking_id: Int): Call<String>

    @FormUrlEncoded
    @POST(Const.API_EDIT_REQUEST)
    fun hitEditRequest(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_UPDATE_CURRENT_LOCATION)
    fun hitUpdateCurrentLocation(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.API_AVAIL_REQUEST_LIST)
    fun apiGetAllRequest(@Query("page") page: Int, @Query("month") month: String, @Query("year") year: String): Call<String>


    @GET(Const.API_MONTHLY_BOOKING_LIST)
    fun apiGetMonthlyList(@Query("month") month: String, @Query("year") year: String, @Query("date") date: String): Call<String>

    @GET(Const.API_AVAILABLE_LIST)
    fun apiAvailabilityList(@Query("date") month: String): Call<String>

    @GET(Const.API_GET_CURRENT_LOCATION)
    fun apiGetCurrentLocation(@Query("id") id: Int, @Query("booking_id") booking_id: Int): Call<String>


    @GET(Const.API_ORDER_SUMMARY)
    fun apiGetOrderSummary(@Query("id") id: Int): Call<String>

    @FormUrlEncoded
    @POST(Const.API_ADD_RATING)
    fun apiAddRating(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_REPORT_PROBLEM)
    fun apiReportProblem(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.API_TRACKING_DATA)
    fun apiGetTracking(@Query("id") id: Int): Call<String>


    @GET(Const.API_AVAIL_CHANGE_STATE)
    fun apiChangeState(@Query("id") id: Int, @Query("state") state: Int, @Query("description") description: String): Call<String>


    @GET(Const.API_AVAIL_DATE_REQUEST_LIST)
    fun apiGetRequestDateWise(@Query("date") date: String): Call<String>

    @GET(Const.API_BOOKING_LIST)
    fun apiGetBookingList(@Query("page") page: Int, @Query("state") state: String): Call<String>

    //language module

    @GET(Const.API_LANGUAGE_LIST)
    fun apiGetLanguageList(@Query("page") page: Int): Call<String>

    @DELETE(Const.API_DELETE_LANGUAGE)
    fun apiLanguageDelete(@Query("id") id: Int): Call<String>

    @GET(Const.API_ADDED_LANGUAGE_LIST)
    fun apiGetAddedLanguageList(@Query("page") page: Int): Call<String>

    @FormUrlEncoded
    @POST(Const.API_ADD_LANGUAGE)
    fun apiAddLanguage(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.API_GET_RATING)
    fun apiGetRating(@Query("id") id: Int, @Query("page") pageCount: Int): Call<String>

    @GET(Const.API_EARNING)
    fun apiGetEarnings(@Query("type") type: Int): Call<String>

    @GET(Const.API_ONLINE_OFFLINE)
    fun apiOFFON(): Call<String>

    @FormUrlEncoded
    @POST(Const.API_SOCIAL_LOGIN)
    fun apiSocialLogin(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_ADD_EXTRA_REQUIREMENT)
    fun apiAddRequirement(@FieldMap serverHashMap: HashMap<String, Any?>?, @Query("category_id") categoryId: Int, @Query("sub_category_id") sub_categoryId: Int): Call<String>

    @POST(Const.API_ADD_WORK_DETAIL)
    fun apiAddWorkDetail(@Body serverHashMap: RequestBody, @Query("id") id: Int): Call<String>

    @DELETE(Const.API_DELETE_WORK_DETAIL_IMAGE)
    fun apiDeleteWorkImage(@Query("id") id: Int): Call<String>

    @GET(Const.API_GET_WORK_DETAIL_IMAGE)
    fun apiGetWorkImage(@Query("id") id: Int): Call<String>

    @GET(Const.API_CANCEL_TRACK)
    fun apiCancelTrack(@Query("id") id: Int): Call<String>

    @GET(Const.API_GET_EXTRA_REQUIREMENT)
    fun apiGetRequirement(@Query("category_id") categoryId: Int, @Query("sub_category_id") sub_categoryId: Int): Call<String>

    @GET(Const.API_PROVIDER_SUB_CATEGORY_LIST)
    fun apiGetSubCategoryList(@Query("type_id") type_id: Int, @Query("page") page: Int): Call<String>


}