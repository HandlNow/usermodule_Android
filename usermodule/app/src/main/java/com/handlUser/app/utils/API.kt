/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.utils

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import kotlin.collections.HashMap

interface API {


    @FormUrlEncoded
    @POST(Const.API_SIGN_UP)
    fun apiSignUp(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_SIGN_UP_CHECK)
    fun apiSignUpCheck(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

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


    //transaction api

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

    @FormUrlEncoded
    @POST(Const.API_UPDATE_ADDRESS)
    fun apiUpdateAddress(@Query("id") id: Int, @FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

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

    @GET(Const.API_PROVIDER_CATEGORY_LIST)
    fun apiGetCategoryList(@Query("page") page: Int, @Query("title") title: String): Call<String>

    @GET(Const.API_PROVIDER_SUB_CATEGORY_LIST)
    fun apiGetSubCategoryList(@Query("type_id") type_id: Int, @Query("page") page: Int): Call<String>

    @FormUrlEncoded
    @POST(Const.API_PROVIDER_ADD_KID)
    fun apiAddKid(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_PROVIDER_ADD_SERVICE)
    fun apiAddService(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>


    @FormUrlEncoded
    @POST(Const.API_PROVIDER_ADD_REQUIREMENT)
    fun apiAddRequirement(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @GET(Const.API_PROVIDER_EXTRA_REQUIREMENT)
    fun apiGetExtraRequirement(@Query("page") page: Int): Call<String>

    @FormUrlEncoded
    @POST(Const.API_PROVIDER_RESULT_LIST)
    fun apiGetResultList(@FieldMap serverHashMap: HashMap<String, Any?>?,@Query("requirement") requirement: Int,@Query("page") page: Int): Call<String>

    @FormUrlEncoded
    @POST(Const.API_PROVIDER_RESULT_DATE_LIST)
    fun apiGetResultDateList(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_AVAIL_BOOKING)
    fun apiReqServiceBooking(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_ADD_RATING)
    fun apiAddRating(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_REPORT_PROBLEM)
    fun apiReportProblem(@FieldMap serverHashMap: HashMap<String, Any?>?): Call<String>

    @FormUrlEncoded
    @POST(Const.API_CONFIRM_EDIT_RESPOND)
    fun apiConfirmRespond(@FieldMap serverHashMap: HashMap<String, Any?>?, @Query("booking_id") id: Int): Call<String>

    @GET(Const.API_GET_RATING)
    fun apiGetRating(@Query("id") id: Int, @Query("page") pageCount: Int): Call<String>

    @GET(Const.API_EDIT_RESPONSE)
    fun apiEditResponse(@Query("booking_id") booking_id: Int): Call<String>

    @GET(Const.API_BOOKING_LIST)
    fun apiGetBookingList(@Query("page") page: Int, @Query("state") state: String): Call<String>

    @GET(Const.API_TRACKING_DATA)
    fun apiGetTracking(@Query("id") id: Int): Call<String>


    @FormUrlEncoded
    @POST(Const.API_UPDATE_CURRENT_LOCATION)
    fun hitUpdateCurrentLocation(@FieldMap serverHashMap: java.util.HashMap<String, Any?>?): Call<String>

    @GET(Const.API_CALENDAR_LIST)
    fun apiAvailabilityList(@Query("date") month: String): Call<String>

    @FormUrlEncoded
    @POST(Const.API_PAYMENT_ORDER)
    fun hitOrderPayment(@FieldMap serverHashMap: java.util.HashMap<String, Any?>?, @Query("card_id") id: String): Call<String>

    @GET(Const.API_AVAIL_CHANGE_STATE)
    fun apiChangeState(@Query("id") id: Int, @Query("state") state: Int, @Query("description") description: String): Call<String>

    @GET(Const.API_MONTHLY_BOOKING_LIST)
    fun apiGetMonthlyList(@Query("month") month: String, @Query("year") year: String): Call<String>

    @GET(Const.API_BOOKING_AVAILABILITY_LIST)
    fun apiGetDayList(@Query("date") month: String): Call<String>

    @GET(Const.API_GET_CURRENT_LOCATION)
    fun apiGetCurrentLocation(@Query("id") id: Int, @Query("booking_id") booking_id: Int): Call<String>

    @GET(Const.API_ORDER_SUMMARY)
    fun apiGetOrderSummary(@Query("id") id: Int): Call<String>

    @FormUrlEncoded
    @POST(Const.API_SOCIAL_LOGIN)
    fun apiSocialLogin(@FieldMap serverHashMap: java.util.HashMap<String, Any?>?): Call<String>

}