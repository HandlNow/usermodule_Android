/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.utils

object
Const {

//    const val SERVER_REMOTE_URL = "http://192.168.2.2/handl-service-provider-application-yii2-1470/"
//    const val IMAGE_URL = "http://192.168.2.2"

    val SERVER_REMOTE_URL = "https://dev.toxsl.in/handl-service-provider-application-yii2-1470/"
    val IMAGE_URL = "https://dev.toxsl.in"

    val LIVE_SERVER_REMOTE_URL = "https://dev.toxsl.in/handl-service-provider-application-yii2-1470/"
    val LIVE_IMAGE_URL = "https://dev.toxsl.in"

    val DELAY_TIMEOUT = 2 * 1000
    val DELAY_ONE_TIMEOUT = 100
    const val THIRTY = 30
    const val ZERO = 0
    const val ONE = 1
    const val HELP = 0
    const val TERMS = 4
    const val PRIVACY = 3
    const val ABOUT = 5
    const val SEE_MORE = 6
    val SOURE: Int = 1224
    const val HOME_CLEANING = 9

    const val DISPLAY_MESSAGE_ACTION = "com.handlService.DISPLAY_MESSAGE"
    const val UPDATE_TOKEN_ACTION = "com.handlService.UPDATE_TOKEN"
    const val PLAY_SERVICES_RESOLUTION_REQUEST = 1234
    const val SPLASH_TIMEOUT = 3000
    const val PERMISSION_ID = 99
    const val STATUS_OK = 200
    const val STATUS = 400
    const val IMAGE_CODE1 = 12345

    const val IMAGE_PERMISSION = 4586
    const val IMAGE_CODE = 1234

    const val DEVICE_TOKEN = "device_token"
    const val PROFILE_DATA = "profile_data"
    const val ADDRESS_DATA = "address_data"
    const val MESSAGE = "message"
    const val LIST = "list"
    const val PAGE_COUNT = "pageCount"
    const val _META = "_meta"
    const val ANDROID = "1"
    const val EIGHT = 8
    const val USERNAME = "com.handlUser.username"
    const val PASSWORD = "com.handlUser.password"

    const val TYPE_ON = 1
    const val TYPE_OFF = 0
    const val IS_NOT_VERIFIED = 0
    const val IS_VERIFIED = 2
    const val IS_VERIFIED_PENDING = 1

    //Api
    const val API_ADD_LANGUAGE = "api/user/add-user-language"
    const val API_LANGUAGE_LIST = "api/user/language-list"
    const val API_ADDED_LANGUAGE_LIST = "api/user/user-language-list"
    const val API_DELETE_LANGUAGE = "api/user/remove-user-language"

    const val API_CONTACT_US = "api/user/contact-us"
    const val API_CHANGE_PASSWORD = "api/user/change-password"
    const val API_CHECK = "api/user/check"
    const val API_SIGN_UP = "api/user/provider-signup"
    const val API_LOGIN = "api/user/provider-login"
    const val API_SOCIAL_LOGIN = "api/user/social-login"
    const val API_LOGOUT = "api/user/logout"
    const val API_VERIFY_OTP = "api/user/verify-otp"
    const val API_RESEND_OTP = "api/user/resend-otp"
    const val API_FORGOT = "api/user/recover"
    const val API_STATIC_PAGE = "api/user/get-page"
    const val API_UPDATE_PROFILE = "api/user/update-profile"
    const val API_DOCUMENT = "api/user/update-proof"
    const val API_NOTIFICATION_LIST = "api/user/notification-list"
    const val API_ADD_BANK = "api/user/create-connect-account"
    const val API_BOOKING_AVAILABILITY_LIST = "api/availability/booking-availability"

    // category list
    const val API_CATEGORY_LIST = "api/category/list"
    const val API_MY_CATEGORY_LIST = "api/category/provider-category-list"
    const val API_MY_CATEGORY_ON_OFF = "api/availability/enable-service"
    const val API_CATEGORY_ADD_PRICE = "api/category/add-provider-category"
    const val API_CATEGORY_GET_PRICE = "api/category/provider-category-detail"
    const val API_CATEGORY_UPDATE_PRICE = "api/category/update-provider-category"
    const val API_PLAN_LIST = "api/plan/list"
    const val API_PLAN_BUY = "api/plan/buy"

    // transaction api
    const val API_CARD_DELETE = "api/transaction/card-delete"
    const val API_CARD_LIST = "api/transaction/card-list"
    const val API_CARD_ADD = "api/transaction/add-card"

    //address list
    const val API_ADD_ADDRESS = "api/user-address/add"
    const val API_ADDRESS_LIST = "api/user-address/address-list"
    const val API_DELETE_ADDRESS = "api/user-address/delete-address"

    // Transportation API

    const val API_TRANSPORTATION_LIST = "api/user/transpotation-list"
    const val API_TRANSPORTATION_DELETE = "api/user/transpotation-delete"
    const val API_TRANSPORTATION_ADD = "api/user/add-transpotation"

    //chat api's
    const val API_SEND_MESSAGE = "api/chat-message/send"
    const val API_OLD_MESSAGES = "api/chat-message/receive-message"
    const val API_NEW_MESSAGES = "api/chat-message/get-message"
    const val API_INBOX_LIST = "api/chat-message/inbox"

    const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    const val DATE_00_00_00 = "00:00:00"
    const val DATE_23_59_59 = "23:59:59"

    //slots api

    const val API_AVAIL_GET_SLOT = "api/availability/get-slot"
    const val API_AVAIL_ADD_SLOT = "api/availability/add"
    const val API_AVAIL_DELETE_SLOT = "api/availability/delete-slot"
    const val API_CANCEL_TRACK = "api/availability/cancel-booking"


    const val API_PROVIDER_SUB_CATEGORY_LIST = "api/category/provider-detail"



    const val STATE_PENDING = 0
    const val STATE_ACCEPT = 1
    const val STATE_REJECT = 2
    const val STATE_CANCEL = 3
    const val STATE_COMPLETE = 4
    const val STATE_EDIT = 5
    const val STATE_ARRIVED_CLIENT = 6
    const val STATE_START_WORK = 7
    const val STATE_END_WORK = 8
    const val STATE_TERMINATE = 9
    const val STATE_START = 10
    const val STATE_STOP = 11
    const val STATE_REVIEWED = 12
    const val STATE_MESSAGE = 0
    const val STATE_IMAGE = 1

    const val IS_RECURRING = 1
    const val NOT_RECURRING = 2
    const val WEEKDAYS_RECURRING = 3
    const val WEEKEND_RECURRING = 4

    const val EARNING_TYPE_DAILY = 1
    const val EARNING_TYPE_WEEKLY = 2
    const val EARNING_TYPE_MONTHLY = 3
    const val EARNING_TYPE_YEARLY = 4

    const val ONE_DAY = 1
    const val EVERY_DAY = 2

    const val API_MONTHLY_BOOKING_LIST = "api/availability/monthly-booking-list"
    const val API_ORDER_SUMMARY = "api/availability/order-summary"
    const val API_AVAIL_REQUEST_LIST = "api/availability/provider-booking-list"
    const val API_AVAIL_DATE_REQUEST_LIST = "api/availability/date-list"
    const val API_AVAIL_CHANGE_STATE = "api/availability/change-state"
    const val API_BOOKING_LIST = "api/availability/booking-list"
    const val API_TRACKING_DATA = "api/availability/track-list"
    const val API_AVAILABLE_LIST = "api/availability/list"
    const val API_EDIT_REQUEST = "api/availability/provider-edit-request"
    const val API_UPDATE_CURRENT_LOCATION = "api/availability/get-current-location"
    const val API_GET_CURRENT_LOCATION = "api/availability/user-current-location"
    const val API_GET_RATING = "api/rating/get-rating"
    const val API_ADD_RATING = "api/rating/add-rating"
    const val API_REPORT_PROBLEM = "api/user/report-problem"
    const val API_EARNING = "api/availability/my-earning"
    const val API_ONLINE_OFFLINE = "api/user/online-offline"
    const val API_ADD_EXTRA_REQUIREMENT = "api/category/add-special-requirement"
    const val API_GET_EXTRA_REQUIREMENT = "api/category/get-special-requirement"
    const val API_ADD_WORK_DETAIL = "api/category/add-provider-images"
    const val API_DELETE_WORK_DETAIL_IMAGE = "api/category/delete-image"
    const val API_GET_WORK_DETAIL_IMAGE = "api/category/get-image"

    val FACEBOOK_LINK = "https://graph.facebook.com/"
    val PICTURE_TYPE = "/picture?type=large"
    val FACEBOOK = "FACEBOOK"
    val GOOGLE = "GOOGLE"
    const val PICTURE = "picture"
    const val DATA = "data"
    const val ID = "id"
    const val URL = "url"

}