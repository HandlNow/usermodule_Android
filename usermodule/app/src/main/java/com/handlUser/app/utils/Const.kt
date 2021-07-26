/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.utils

object Const {

    const val SERVER_REMOTE_URL = "https://dev.toxsl.in/handl-service-provider-application-yii2-1470/"
    const val IMAGE_URL = "https://dev.toxsl.in"

//    const val SERVER_REMOTE_URL = "http://192.168.2.2/handl-service-provider-application-yii2-1470/"
//    const val IMAGE_URL = "http://192.168.2.2"

    const val LIVE_SERVER_REMOTE_URL = "https://dev.toxsl.in/handl-service-provider-application-yii2-1470/"
    const val LIVE_IMAGE_URL = "https://dev.toxsl.in"


    const val DELAY_TIMEOUT = 6 * 1000
    const val DELAY_ONE_TIMEOUT = 100
    const val TERMS = 4
    const val PRIVACY = 3
    const val ABOUT = 5
    const val SEE_MORE = 6
    const val THIRTY = 30
    const val ZERO = 0
    const val ONE = 1
    const val TWO = 2
    const val THREE = 3
    const val FOUR = 4
    const val SOURE: Int = 1224
    const val STRIPE_PUBLISH_KEY = "pk_test_51Hye5dKvFHVVbCUc2o4PGIGvMR0GKbAnRGsIJmx2sXqKczpYYJDqaUjipThe39qkocu6zNgomgsJ39QFQyVr4ksW00CwOn15dg"
    const val IMAGE_CODE1 = 12345

    const val DISPLAY_MESSAGE_ACTION = "com.handlUser.DISPLAY_MESSAGE"
    const val UPDATE_TOKEN_ACTION = "com.handlUser.UPDATE_TOKEN"
    const val PLAY_SERVICES_RESOLUTION_REQUEST = 1234
    const val SPLASH_TIMEOUT = 3000
    const val PERMISSION_ID = 99
    const val STATUS_OK = 200

    var IMAGE_PERMISSION = 4586
    const val TYPE_ON = 1
    const val TYPE_OFF = 0
    const val DEVICE_TOKEN = "device_token"
    const val FILTER_DATA = "filter_data"
    const val PROFILE_DATA = "profile_data"
    const val ADDRESS_DATA = "address_data"
    const val HOME_LAT = "home_lat"
    const val HOME_ADD = "home_add"
    const val HOME_LONG = "home_long"
    const val LIST = "list"
    const val PAGE_COUNT = "pageCount"
    const val _META = "_meta"
    const val MESSAGE = "message"
    const val ANDROID = "1"
    const val EIGHT = 8
    const val USERNAME = "com.handlUser.username"
    const val PASSWORD = "com.handlUser.password"

    const val WALL_PAINT = 1
    const val GARDEN_SERVICE = 2
    const val FURNITURE_ASSEMBLING = 3
    const val NANNY = 4
    const val SCHOOL_TUTOR = 5
    const val PET_WALKING = 6
    const val NUTRITION = 7
    const val MAKEUP = 8
    const val HOME_CLEANING = 9
    const val MATTRESS_CLEANING = 10
    const val AC_CLEANING = 11
    const val PEST_CONTROLL = 12
    const val CAR_WASH = 13
    const val DISINFECTION = 14
    const val EXTRA_REQUIREMENT = 15

    const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

    //Api
    const val API_CONTACT_US = "api/user/contact-us"
    const val API_CHANGE_PASSWORD = "api/user/change-password"
    const val API_CHECK = "api/user/check"
    const val API_SIGN_UP = "api/user/user-signup"
    const val API_SIGN_UP_CHECK = "api/user/signup-check"
    const val API_LOGIN = "api/user/user-login"
    const val API_SOCIAL_LOGIN = "api/user/social-login"
    const val API_LOGOUT = "api/user/logout"
    const val API_VERIFY_OTP = "api/user/verify-otp"
    const val API_RESEND_OTP = "api/user/resend-otp"
    const val API_FORGOT = "api/user/recover"
    const val API_STATIC_PAGE = "api/user/get-page"
    const val API_UPDATE_PROFILE = "api/user/update-profile"

    // transaction api
    const val API_CARD_DELETE = "api/transaction/card-delete"
    const val API_CARD_LIST = "api/transaction/card-list"
    const val API_CARD_ADD = "api/transaction/add-card"


    //address list
    const val API_ADD_ADDRESS = "api/user-address/add"
    const val API_UPDATE_ADDRESS = "api/user-address/update"
    const val API_ADDRESS_LIST = "api/user-address/address-list"
    const val API_DELETE_ADDRESS = "api/user-address/delete-address"

    const val API_SEND_MESSAGE = "api/chat-message/send"
    const val API_OLD_MESSAGES = "api/chat-message/receive-message"
    const val API_NEW_MESSAGES = "api/chat-message/get-message"
    const val API_INBOX_LIST = "api/chat-message/inbox"

    const val API_NOTIFICATION_LIST = "api/user/notification-list"
    const val API_BOOKING_LIST = "api/availability/booking-list"

    const val API_AVAIL_BOOKING = "api/availability/booking"
    const val API_TRACKING_DATA = "api/availability/track-list"

    const val API_PROVIDER_CATEGORY_LIST = "api/availability/user-list"
    const val API_PROVIDER_SUB_CATEGORY_LIST = "api/category/sub-category-detail"
    const val API_PROVIDER_EXTRA_REQUIREMENT = "api/category/extra-requirement"
    const val API_PROVIDER_ADD_KID = "api/category/add-kid"
    const val API_PROVIDER_ADD_REQUIREMENT = "api/availability/user-requirement"
    const val API_PROVIDER_ADD_SERVICE = "api/availability/user-service"
    const val API_PROVIDER_RESULT_LIST = "api/category/available-provider"
    const val API_PROVIDER_RESULT_DATE_LIST = "api/category/result-date"
    const val API_GET_RATING = "api/rating/get-rating"
    const val API_ADD_RATING = "api/rating/add-rating"
    const val API_REPORT_PROBLEM = "api/user/report-problem"
    const val API_EDIT_RESPONSE = "api/availability/respond"
    const val API_UPDATE_CURRENT_LOCATION = "api/availability/get-current-location"
    const val API_GET_CURRENT_LOCATION = "api/availability/user-current-location"
    const val API_AVAIL_CHANGE_STATE = "api/availability/change-state"
    const val API_MONTHLY_BOOKING_LIST = "api/availability/monthly-booking-list"
    const val API_ORDER_SUMMARY = "api/availability/order-summary"
    const val API_PAYMENT_ORDER = "api/transaction/payment"
    const val API_CONFIRM_EDIT_RESPOND = "api/availability/confirm"
    const val API_CALENDAR_LIST = "api/availability/user-booking"
    const val API_BOOKING_AVAILABILITY_LIST = "api/availability/booking-availability"

    const val STATE_PENDING = 0
    const val STATE_ACCEPT = 1
    const val STATE_REJECT = 2
    const val STATE_CANCEL = 3
    const val STATE_EDIT = 5
    const val STATE_START_WORK = 7
    const val STATE_END_WORK = 8
    const val STATE_TERMINATE = 9
    const val STATE_START = 10
    const val STATE_STOP = 11
    const val STATE_REVIEWED = 12

    const val STATE_MESSAGE = 0
    const val STATE_IMAGE = 1

    const val FACEBOOK_LINK = "https://graph.facebook.com/"
    const val PICTURE_TYPE = "/picture?type=large"
    const val FACEBOOK = "FACEBOOK"
    const val GOOGLE = "GOOGLE"
    const val PICTURE = "picture"
    const val DATA = "data"
    const val ID = "id"
    const val URL = "url"

    //filter const
    const val PRICE_HIGH_TO_LOW = 1
    const val PRICE_LOW_TO_HIGH = 2
    const val MOST_POPULAR = 3
    const val NEAR_BY = 4
    const val HIGHEST_REVIEW = 5
}