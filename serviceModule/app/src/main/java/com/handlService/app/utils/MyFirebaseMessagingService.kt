/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PixelFormat
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.*
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.handlService.app.BuildConfig
import com.handlService.app.R
import com.handlService.app.ui.activity.BaseApp
import com.handlService.app.ui.activity.MainActivity
import org.json.JSONObject

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val prefStore = PrefStore(this)
    private var BaseApplication: BaseApp? = null

    private fun displayMessage(extras: Bundle) {
        val intent = Intent(Const.DISPLAY_MESSAGE_ACTION)
        intent.putExtra("detail", extras)
        sendBroadcast(intent)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val intent = Intent(Const.UPDATE_TOKEN_ACTION)
        intent.putExtra("firebase_token", p0)
        this.sendBroadcast(intent)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        prefStore.saveString(Const.DEVICE_TOKEN, p0)

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        BaseApplication = applicationContext as BaseApp
        if (remoteMessage.notification != null) Log.e(TAG, remoteMessage.notification!!.title as String) else Log.e("notification payload", "no payload")
        val data = remoteMessage.data
        if (BuildConfig.DEBUG) {
            Log.e("TAG Notification", data.toString())
        }
        val bundle = Bundle()
        for ((key, value) in remoteMessage.data) {
            bundle.putString(key, value)
        }

        sendNotification(bundle, this)
        displayMessage(bundle)

    }


    @SuppressLint("InvalidWakeLockTag")
    private fun sendNotification(extra: Bundle, context: MyFirebaseMessagingService) {
        val noti_id = 10
        extra.putInt("notificationId", noti_id)
        val name: String?
        name = getString(R.string.app_name)

        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_notifications_active_24)
        val mBuilder = NotificationCompat.Builder(context, PRIMARY_CHANNEL)
                .setSmallIcon( R.drawable.ic_baseline_notifications_active_24)
                .setLargeIcon(largeIcon)
                .setContentTitle(name)
                .setContentText(extra.get("message") as String)

        val NotiSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        mBuilder.setSound(NotiSound)
        val vibrate = longArrayOf(0, 100, 200, 300)
        mBuilder.setVibrate(vibrate)
        val resultIntent = Intent(context, MainActivity::class.java)

        resultIntent.action = Intent.ACTION_MAIN
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        resultIntent.putExtra("detail", extra)
        val resultPendingIntent = PendingIntent.getActivity(applicationContext, noti_id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setWhen(System.currentTimeMillis())
        mBuilder.setContentIntent(resultPendingIntent)
        val mPowerManager = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        var wl: PowerManager.WakeLock? = null
        wl = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag")
        wl?.acquire(1000)
        val mNotificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") val chan1 = NotificationChannel(PRIMARY_CHANNEL,
                    "news", NotificationManager.IMPORTANCE_DEFAULT)
            chan1.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            chan1.lightColor = Color.GREEN
            chan1.setShowBadge(true)
            mNotificationManager.createNotificationChannel(chan1)
        }
        mNotificationManager.notify(System.currentTimeMillis().toInt(), mBuilder.build())
    }


    companion object {
        const val PRIMARY_CHANNEL = "default"
        private const val TAG = "NotificationService"
    }


}