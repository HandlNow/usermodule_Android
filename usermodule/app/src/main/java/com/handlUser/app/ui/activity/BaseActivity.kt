/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.*
import android.provider.Settings
import android.text.method.ScrollingMovementMethod
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.handlUser.app.BuildConfig
import com.handlUser.app.R
import com.handlUser.app.databinding.DialogLocationTurnonBinding
import com.handlUser.app.model.AddressData
import com.handlUser.app.model.FilterData
import com.handlUser.app.model.ProfileData
import com.handlUser.app.service.LocationUpdateService
import com.handlUser.app.ui.snackBar.ActionClickListener
import com.handlUser.app.ui.snackBar.Snackbar
import com.handlUser.app.ui.snackBar.SnackbarManager
import com.handlUser.app.ui.snackBar.SnackbarType
import com.handlUser.app.utils.*
import com.jakewharton.threetenabp.AndroidThreeTen
import com.toxsl.restfulClient.api.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


open class BaseActivity : AppCompatActivity(), SyncEventListener, View.OnClickListener {

    var restFullClient: RestFullClient? = null
    var api: API? = null
    var apiInstance: Retrofit? = null

    var inflater: LayoutInflater? = null
    var store: PrefStore? = null
    var permCallback: PermCallback? = null
    private var progressDialog: Dialog? = null
    private var txtMsgTV: TextView? = null
    private var reqCode: Int = 0
    private var networksBroadcast: NetworksBroadcast? = null
    private val networkAlertDialog: AlertDialog? = null
    private var networkStatus: String? = null
    private var inputMethodManager: InputMethodManager? = null
    private var failureDailog: AlertDialog.Builder? = null
    private var failureAlertDialog: AlertDialog? = null
    private var exit: Boolean = false
    var googleApiHandle: GoogleApisHandle? = null
    var locationService: LocationUpdateService? = null

    val uniqueDeviceId: String
        @SuppressLint("HardwareIds")
        get() = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager
                    .activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        restFullClient = RestFullClient.getInstance(this)
        apiInstance = if (BuildConfig.DEBUG) {
            restFullClient!!.getRetrofitInstance(Const.SERVER_REMOTE_URL)
        } else {
            restFullClient!!.getRetrofitInstance(Const.LIVE_SERVER_REMOTE_URL)
        }
        api = apiInstance!!.create(API::class.java)

        this@BaseActivity.overridePendingTransition(R.anim.slide_in,
                R.anim.slide_out)
        inputMethodManager = this
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        store = PrefStore(this)
        initializeNetworkBroadcast()
        FirebaseApp.initializeApp(this)
        strictModeThread()
        transitionSlideInHorizontal()
        progressDialog()
        failureDailog = AlertDialog.Builder(this,R.style.animateDialog)
        Places.initialize(this, getString(R.string.google_api))
        googleApiHandle = GoogleApisHandle.getInstance(this)
        getToken()
        locationService = LocationUpdateService.getInstance()
        AndroidThreeTen.init(this)


    }


    fun initFCM() {
        if (checkPlayServices()) {
            if (restFullClient?.getLoginStatus() != null) {
                checkApi()
            } else {
                gotoLoginSignUpActivity()
            }
        }


    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token
                .addOnCompleteListener(object : OnCompleteListener<String?> {
                    override fun onComplete(task: Task<String?>) {
                        if (!task.isSuccessful) {
                            if (BuildConfig.DEBUG) {
                                Log.w("TAG", "Fetching FCM registration token failed", task.getException())
                            }
                            return
                        }

                        // Get new FCM registration token
                        if (task.isSuccessful && task.result != null) {
                            store!!.saveString(Const.DEVICE_TOKEN, task.result!!)
                        }
                    }
                })
    }

    fun getDeviceToken(): String {
        return if (store!!.getString(Const.DEVICE_TOKEN, "")!!.isEmpty()) {
            uniqueDeviceId
        } else {
            store!!.getString(Const.DEVICE_TOKEN)!!
        }
    }

    private fun checkApi() {
        val params = Api3Params()
        params.put("DeviceDetail[device_token]", getDeviceToken())
        params.put("DeviceDetail[device_type]", Const.ANDROID)
        params.put("DeviceDetail[device_name]", Build.MODEL)
        val call = api!!.apiCheck(params.getServerHashMap())
        restFullClient?.sendRequest(call, this)

    }


    fun gotoLoginSignUpActivity() {
        startActivity(Intent(this, LoginSignUpActivity::class.java))
        finish()
    }

    open fun setToolbar(title: String?, screenBg: Int = ContextCompat.getColor(this, R.color.screen_bg), showTitle: Boolean = true) {

    }


    fun gotoMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    open fun updateDrawer() {
    }

    private fun initializeNetworkBroadcast() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        networksBroadcast = NetworksBroadcast()
        registerReceiver(networksBroadcast, intentFilter)
    }


    private fun unregisterNetworkBroadcast() {
        try {
            if (networksBroadcast != null) {
                unregisterReceiver(networksBroadcast)
            }
        } catch (e: IllegalArgumentException) {
            networksBroadcast = null
        }

    }

    private fun showNoNetworkDialog(status: String?) {
        networkStatus = status
        if (SnackbarManager.currentSnackbar != null) {
            SnackbarManager.currentSnackbar!!.dismiss()
        }
        SnackbarManager.create(
                Snackbar.with(this)
                        .type(SnackbarType.SINGLE_LINE)
                        .text(status!!).duration(com.handlUser.app.ui.snackBar.Snackbar
                                .SnackbarDuration.LENGTH_INDEFINITE)
                        .actionLabel(getString(R.string.retry_caps))
                        .actionListener(object : ActionClickListener {
                            override fun onActionClicked(snackbar: com.handlUser.app.ui.snackBar.Snackbar) {
                                if (!isNetworkAvailable) {
                                    showNoNetworkDialog(networkStatus)
                                } else
                                    SnackbarManager.currentSnackbar!!.dismiss()
                            }
                        }))!!.show()
    }

    fun changeDateFormat(dateString: String?, sourceDateFormat: String, targetDateFormat: String): String {
        if (dateString == null || dateString.isEmpty()) {
            return ""
        }
        val inputDateFromat = SimpleDateFormat(sourceDateFormat, Locale.getDefault())
        var date = Date()
        try {
            date = inputDateFromat.parse(dateString)
        } catch (e: ParseException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        val outputDateFormat = SimpleDateFormat(targetDateFormat, Locale.getDefault())
        return outputDateFormat.format(date)
    }

    fun changeDateFormatFromDate(sourceDate: Date?, targetDateFormat: String?): String {
        if (sourceDate == null || targetDateFormat == null || targetDateFormat.isEmpty()) {
            return ""
        }
        val outputDateFormat = SimpleDateFormat(targetDateFormat, Locale.getDefault())
        return outputDateFormat.format(sourceDate)
    }

    protected fun checkDate(checkDate: String) {
        val cal = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        var serverDate: Date? = null
        try {
            serverDate = dateFormat.parse(checkDate)
            cal.time = serverDate
            val currentcal = Calendar.getInstance()
            if (currentcal.after(cal)) {
                val builder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.animateDialog)
                builder.setMessage(getString(R.string.contact_company_info))
                builder.setTitle(getString(R.string.demo_expired))
                builder.setCancelable(false)
                builder.setNegativeButton(getString(R.string.close)) { _, _ -> exitFromApp() }
                val alert = builder.create()
                alert.show()
            }
        } catch (e: ParseException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

    }

    fun logOut() {
        AlertDialog.Builder(this, R.style.animateDialog)
                .setTitle(getString(R.string.alert))
                .setMessage(getString(R.string.do_you_want_longout))
                .setPositiveButton(getString(R.string.yes)) { d, i ->
                    val call = api!!.apiLogout()
                    restFullClient?.sendRequest(call, this)
                    d.dismiss()
                }
                .setNegativeButton(getString(R.string.no)) { d, i ->
                    d.dismiss()
                }.show()
    }


    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Const.PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show()
            } else {
                log(getString(R.string.this_device_is_not_supported))
                finish()
            }
            return false
        }
        return true
    }

    fun checkPermissions(perms: Array<String>, requestCode: Int, permCallback: PermCallback): Boolean {
        this.permCallback = permCallback
        this.reqCode = requestCode
        val permsArray = ArrayList<String>()
        var hasPerms = true
        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                permsArray.add(perm)
                hasPerms = false
            }
        }
        if (!hasPerms) {
            val permsString = arrayOfNulls<String>(permsArray.size)
            for (i in permsArray.indices) {
                permsString[i] = permsArray[i]
            }
            ActivityCompat.requestPermissions(this@BaseActivity, permsString, Const.PERMISSION_ID)
            return false
        } else
            return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        var permGrantedBool = false
        when (requestCode) {
            Const.PERMISSION_ID -> {
                for (grantResult in grantResults) {
                    if (grantResult == PackageManager.PERMISSION_DENIED) {
                        showToast(getString(R.string.not_sufficient_permissions)
                                + getString(R.string.app_name)
                                + getString(R.string.permissionss))
                        permGrantedBool = false
                        break
                    } else {
                        permGrantedBool = true
                    }
                }
                if (permCallback != null) {
                    if (permGrantedBool) {
                        permCallback!!.permGranted(reqCode)
                    } else {
                        permCallback!!.permDenied(reqCode)
                    }
                }
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun exitFromApp() {
        finish()
        finishAffinity()
    }

    fun hideSoftKeyboard(): Boolean {
        try {
            if (currentFocus != null) {
                inputMethodManager!!.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
                return true
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        return false
    }

    fun isValidMail(email: String): Boolean {
        return email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex())
    }

    fun isValidPassword(password: String): Boolean {
        return password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[!&^%$#@()=*/.+_-])(?=\\S+$).{8,}$".toRegex())
    }

    fun keyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                if (BuildConfig.DEBUG) {
                    Log.e("KeyHash:>>>>>>>>>>>>>>>", "" + Base64.encodeToString(md.digest(), Base64.DEFAULT))
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        } catch (e: NoSuchAlgorithmException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

    }

    fun log(string: String) {
        if (BuildConfig.DEBUG) {
            Log.e("BaseActivity", string)
        }
    }


    fun log(title: String, message: String?) {
        if (BuildConfig.DEBUG) {
            Log.e(title, message ?: "")
        }
    }

    private fun progressDialog() {
        progressDialog = Dialog(this)
        val view = View.inflate(this, R.layout.progress_dialog, null)
        progressDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        progressDialog!!.setContentView(view)
        progressDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog!!.setCancelable(false)
    }

    fun startProgressDialog() {
        if (progressDialog != null && !progressDialog!!.isShowing) {
            try {
                progressDialog!!.show()
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

        }
    }

    fun stopProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing) {
            try {
                progressDialog!!.dismiss()
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

        }
    }

    override fun onSyncStart() {
        startProgressDialog()
    }

    override fun onSyncFinish() {
        stopProgressDialog()
    }

    open fun errorSnackBar(errorString: String, call: Call<String>?, callBack: Callback<String>?): SnackbarManager? {
        val buttontext: String
        buttontext = if (call != null && callBack != null) {
            getString(R.string.retry_cap)
        } else {
            getString(R.string.exit_caps)
        }
        val snackBar: Snackbar = Snackbar.with(this)
                .type(SnackbarType.MULTI_LINE)
                .text(errorString)
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .actionLabel(buttontext)
                .actionListener(object : ActionClickListener {
                    override fun onActionClicked(snackbar: Snackbar) {
                        if (call != null && callBack != null) {
                            onSyncStart()
                            call.clone().enqueue(callBack)
                        } else {
                            finish()
                        }
                    }

                })
        return SnackbarManager.create(snackBar)
    }

    override fun onSyncFailure(errorCode: Int, t: Throwable?, response: Response<String>?, call: Call<String>?, callBack: Callback<String>?) {
        log("error_message", if (response != null) response.message() else "")
        log("error_code", errorCode.toString())
        if (this.isFinishing) return
        if (failureAlertDialog != null && failureAlertDialog!!.isShowing) {
            failureAlertDialog!!.dismiss()
        }
        if (errorCode == HTTPS_RESPONSE_CODE.FORBIDDEN_ERROR || errorCode == HTTPS_RESPONSE_CODE.UN_AUTHORIZATION) {
            log(getString(R.string.error), getString(R.string.session_timeout_redirecting))
            showToast(getString(R.string.session_timeout_redirecting))
            restFullClient!!.setLoginStatus(null)
            gotoLoginSignUpActivity()
            setProfileData(null)
            locationService?.stopService(this)
        } else if (errorCode == HTTPS_RESPONSE_CODE.SERVER_ERROR) {
            showToast(getString(R.string.problem_connecting_to_the_server))
        } else if (t is SocketTimeoutException || t is ConnectException) {
            log(getString(R.string.error), getString(R.string.request_timeout_slow_connection))
            errorSnackBar(getString(R.string.request_timeout_slow_connection), call, callBack)!!.show()
        } else if (t is AppInMaintenance) {
            log(getString(R.string.error), getString(R.string.api_is_in_maintenance))
            failureErrorDialog(t.message!!, call, callBack)!!.show()
        } else if (t is AppExpiredError) {
            log(getString(R.string.error), getString(R.string.api_is_expired))
        } else {
            log(getString(R.string.error), if (response != null) response.message() else if (t != null) t.message else "")
            var message = getString(R.string.problem_connecting_to_the_server)
            try {
                val json = JSONObject(response?.body()?.toString()
                        ?: response?.errorBody()?.string() ?: "{'message':'$message'}")
                if (json.has("message")) message = json.getString("message") else if (json.has("error")) message = json.getString("error")
            } catch (e: java.lang.Exception) {
                handleException(e)
            }
            showToastOne(message)
        }

    }


    private fun failureErrorDialog(errorString: String, call: Call<String>?, callBack: Callback<String>?): AlertDialog? {
        if (call != null && callBack != null) {
            failureDailog!!.setMessage(errorString).setCancelable(false).setNegativeButton(getString(R.string.exit_caps)) { dialog, which -> finish() }.setPositiveButton(getString(R.string.retry_cap)) { dialog, which ->
                onSyncStart()
                call.clone().enqueue(callBack)
            }
        } else failureDailog!!.setMessage(errorString).setCancelable(false).setPositiveButton(getString(R.string.exit_caps)) { dialog, which -> finish() }
        failureAlertDialog = failureDailog!!.create()
        return failureAlertDialog
    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        try {
            val respObject = JSONObject(response!!)
            if (responseUrl.contains(Const.API_LOGOUT)) {
                showToastOne(getString(R.string.logout_sucess))
                setProfileData(null)
                restFullClient!!.setLoginStatus(null)
                gotoLoginSignUpActivity()
            } else if (responseUrl.contains(Const.API_CHECK)) {
                val data = Gson().fromJson<ProfileData>(respObject.getJSONObject("detail").toString(), ProfileData::class.java)
                setProfileData(data)
                restFullClient!!.setLoginStatus(data.accessToken)
                if (getProfileData()!!.isUserAddress && getProfileData()!!.isUserPayment) {
                    gotoMainActivity()
                } else {
                    gotoLoginSignUpActivity()
                }
            }

        } catch (e: JSONException) {
            handleException(e)
        }
    }

    fun showToast(msg: String) {
        SnackbarManager.create(
                Snackbar.with(this).duration(Snackbar.SnackbarDuration.LENGTH_SHORT)
                        .type(SnackbarType.MULTI_LINE)
                        .text(msg))!!.show()
    }

    fun showToastOne(msg: String) {
        val layout: View = layoutInflater.inflate(R.layout.layout_toast,
                findViewById(R.id.toast_layout_root))
        val text = layout.findViewById<View>(R.id.toastTV) as TextView
        text.text = msg
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    private fun strictModeThread() {
        val policy = StrictMode.ThreadPolicy.Builder()
                .permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }

    private fun transitionSlideInHorizontal() {
        this.overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left)
    }

    override fun onClick(v: View) {

    }

    override fun onDestroy() {
        unregisterNetworkBroadcast()
        LocationUpdateService.getInstance().stopService(this)
        super.onDestroy()
    }

    fun backAction() {
        if (exit) {
            finishAffinity()
        } else {
            showToastOne(getString(R.string.press_one_more_time))
            exit = true
            Handler(Looper.getMainLooper()).postDelayed({ exit = false }, (2 * 1000).toLong())
        }
    }

    interface PermCallback {
        fun permGranted(resultCode: Int)

        fun permDenied(resultCode: Int)
    }

    inner class NetworksBroadcast : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val status = NetworkUtil.getConnectivityStatusString(context)
            if (status == null && networkAlertDialog != null) {
                networkStatus = null
                networkAlertDialog.dismiss()
            } else if (status != null && networkStatus == null)
                showNoNetworkDialog(status)
        }
    }

    open fun handleException(e: java.lang.Exception) {
        if (BuildConfig.DEBUG) {
            e.printStackTrace()
        }
    }

    fun setProfileData(value: ProfileData?) {
        store!!.saveString(Const.PROFILE_DATA, Gson().toJson(value, ProfileData::class.java))
    }

    fun getProfileData(): ProfileData? {
        return Gson().fromJson(store!!.getString(Const.PROFILE_DATA), ProfileData::class.java)
    }

    fun setFilterData(value: FilterData?) {
        store!!.saveString(Const.FILTER_DATA, Gson().toJson(value, FilterData::class.java))
    }

    fun getFilterData(): FilterData? {
        return Gson().fromJson(store!!.getString(Const.FILTER_DATA), FilterData::class.java)
    }

    fun setAddressData(value: AddressData?) {
        store!!.saveString(Const.ADDRESS_DATA, Gson().toJson(value, AddressData::class.java))
    }

    fun getAddressData(): AddressData? {
        return Gson().fromJson(store!!.getString(Const.ADDRESS_DATA), AddressData::class.java)
    }


    @SuppressLint("SimpleDateFormat")
    fun changeDateFormatToGmt(dateString: String?, sourceDateFormat: String, targetDateFormat: String): String {
        if (dateString == null || dateString.isEmpty()) {
            return ""
        }
        val inputDateFormat = SimpleDateFormat(sourceDateFormat)
        var date = Date()
        try {
            date = inputDateFormat.parse(dateString)!!
        } catch (e: ParseException) {
            if (androidx.databinding.library.BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        val outputDateFormat = SimpleDateFormat(targetDateFormat)
        outputDateFormat.timeZone = TimeZone.getTimeZone("GMT 00:00")
        return outputDateFormat.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun changeDateFormatGmtToLocal(dateString: String?, sourceDateFormat: String, targetDateFormat: String): String {
        if (dateString == null || dateString.isEmpty()) {
            return ""
        }
        val inputDateFormat = SimpleDateFormat(sourceDateFormat)
        inputDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        var date = Date()
        try {
            date = inputDateFormat.parse(dateString)!!
        } catch (e: ParseException) {
            if (androidx.databinding.library.BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        val outputDateFormat = SimpleDateFormat(targetDateFormat)
        return outputDateFormat.format(date)
    }

    fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(textView.context, color), PorterDuff.Mode.SRC_IN)
            }
        }
    }

    fun clearNotification() {
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
    }

    private var gpsAlert: AlertDialog? = null

    fun buildAlertMessageNoGps() {
        if (gpsAlert != null && gpsAlert!!.isShowing) {
            gpsAlert!!.dismiss()
        }
        val alert = AlertDialog.Builder(this, R.style.animateDialog)
        alert.setTitle(getString(R.string.enable_gps))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), fun(dialog: Any, id: Any) {
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Const.SOURE)
                })
                .setNegativeButton(getString(android.R.string.no)) { dialog, id -> dialog.dismiss() }
        gpsAlert = alert.create()
        gpsAlert!!.show()
        gpsAlert!!.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundResource(0)
        gpsAlert!!.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        gpsAlert!!.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundResource(0)
        gpsAlert!!.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorAccent))

    }

    private var valueAnimator: ValueAnimator? = null
    open fun animateMarker(destination: LatLng, marker: Marker?, googleMap: GoogleMap) {
        if (marker != null) {
            val startPosition = marker.position
            val endPosition = LatLng(destination.latitude, destination.longitude)
            if (valueAnimator != null) valueAnimator!!.end()
            val latLngInterpolator: LatLngInterpolator = LatLngInterpolator.LinearFixed()
            valueAnimator = ValueAnimator.ofFloat(0f, 1f)
            valueAnimator!!.duration = 1000 // duration 1 second
            valueAnimator!!.interpolator = LinearInterpolator()
            valueAnimator!!.addUpdateListener { animation ->
                try {
                    val v = animation.animatedFraction
                    val newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition)
                    marker.position = newPosition
                    marker.rotation = Utilities.computeRotation(
                            v, marker.rotation,
                            Utilities.bearingBetweenLocations(
                                    startPosition,
                                    newPosition
                            ).toFloat()
                    )
                    marker.setAnchor(0.5f, 0.5f)
                    //when marker goes out from screen it automatically move into center
                    if (!Utilities.isMarkerVisible(googleMap, newPosition)) {
                        googleMap.animateCamera(
                                CameraUpdateFactory
                                        .newCameraPosition(
                                                CameraPosition.Builder()
                                                        .target(newPosition)
                                                        .zoom(googleMap.cameraPosition.zoom)
                                                        .build()
                                        )
                        )
                    } else {
                        try {
                            googleMap.animateCamera(
                                    CameraUpdateFactory
                                            .newCameraPosition(
                                                    CameraPosition.Builder()
                                                            .target(newPosition)
                                                            .tilt(0f)
                                                            .zoom(16f)
                                                            .build()
                                            )
                            )
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    // handle exception here
                }
            }
            valueAnimator!!.start()
        }
    }

    fun getTIME(createdOn: Date?): String {
        val simpleDateFormat = SimpleDateFormat(Const.DATE_FORMAT)
        val cal = Calendar.getInstance()
        cal.time = createdOn!!
        val calData = simpleDateFormat.parse(changeDateFormatFromDate(cal.time, Const.DATE_FORMAT))
        val diff: Long = calData!!.time - simpleDateFormat.parse(changeDateFormatFromDate(Calendar.getInstance().time, Const.DATE_FORMAT)).time
        val day = (diff / (1000 * 60 * 60 * 24)).toInt()
        val hour = ((diff - 1000 * 60 * 60 * 24 * day) / (1000 * 60 * 60)).toInt()
        val mins = (diff - 1000 * 60 * 60 * 24 * day - 1000 * 60 * 60 * hour).toInt() / (1000 * 60)

        var times = ""
        if (day > 0 && day == 1) {
            times = "$day${getString(R.string.dayy)}"
        } else if (day > 1) {
            times = "$day${getString(R.string.days)}"
        }
        if (hour > 0 && hour == 1) {
            times = "$times$hour${getString(R.string.hourss)}"
        } else if (hour > 1) {
            times = "$times$hour${getString(R.string.hours)}"
        }
        if (mins > 0 && mins == 1) {
            times = "$times$mins${getString(R.string.minutess)}"
        } else if (mins > 1) {
            times = "$times$mins${getString(R.string.minutes)}"
        }
        if (times.isNotBlank()){
                    return     """$times${getString(R.string.left_to_respond)}"""
        }else{
            return getString(R.string.time_exceeded)
        }

    }

    fun getTimefromDate(startTime: String): String {
        val simpleDateFormat = SimpleDateFormat(Const.DATE_FORMAT)

        val date1 = simpleDateFormat.parse(changeDateFormatGmtToLocal(startTime, Const.DATE_FORMAT, Const.DATE_FORMAT))
        val date2 = simpleDateFormat.parse(changeDateFormatFromDate(Calendar.getInstance().time, Const.DATE_FORMAT))

        val difference: Long = date1!!.time - date2!!.time
        val day = (difference / (1000 * 60 * 60 * 24)).toInt()
        val hour = ((difference - 1000 * 60 * 60 * 24 * day) / (1000 * 60 * 60)).toInt()
        val mins = (difference - 1000 * 60 * 60 * 24 * day - 1000 * 60 * 60 * hour).toInt() / (1000 * 60)

        var times = ""
        if (day > 0 && day == 1) {
            times = "$day${getString(R.string.dayy)}"
        } else if (day > 1) {
            times = "$day${getString(R.string.days)}"
        }
        if (hour > 0 && hour == 1) {
            times = "$times$hour${getString(R.string.hourss)}"
        } else if (hour > 1) {
            times = "$times$hour${getString(R.string.hours)}"
        }
        if (mins > 0 && mins == 1) {
            times = "$times$mins${getString(R.string.minutess)}"
        } else if (mins > 1) {
            times = "$times$mins${getString(R.string.minutes)}"
        }

        return times
    }
    private var dBinding: DialogLocationTurnonBinding? = null

     fun showAlertDialog() {
        val mBuilder = AlertDialog.Builder(this, R.style.animateDialog)
        dBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.dialog_location_turnon, null, false)
        mBuilder.setView(dBinding!!.root)
        mBuilder.setCancelable(false)
        val alertDialogd = mBuilder.create()
        alertDialogd.show()
        dBinding!!.noTV.setOnClickListener {
            alertDialogd.dismiss()
        }
        dBinding!!.yesTV.setOnClickListener {
            alertDialogd.dismiss()
            startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 151)
        }
    }




}
