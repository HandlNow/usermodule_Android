/*
 * @copyright : TOXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of TOXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.extensions

import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.handlUser.app.BuildConfig
import com.handlUser.app.R
import com.handlUser.app.utils.Const


fun ImageView.loadFromUrl(baseActivity: Context, string: String) {
    val url = if (BuildConfig.DEBUG) {
        Const.IMAGE_URL
    } else {
        Const.LIVE_IMAGE_URL
    }

    val requestOptions = RequestOptions()
    Glide.with(baseActivity)
        .load(url + "" + string)
        .placeholder(R.mipmap.ic_default).signature(ObjectKey(System.currentTimeMillis()))
        .error(R.mipmap.ic_default)
        .apply(requestOptions)
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)

}

fun ImageView.loadForCate(baseActivity: Context, string: String) {
    val url = if (BuildConfig.DEBUG) {
        Const.IMAGE_URL
    } else {
        Const.LIVE_IMAGE_URL
    }
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 30f
    circularProgressDrawable.start()
    val requestOptions = RequestOptions()
    Glide.with(baseActivity)
        .load(url + "" + string)
        .placeholder(circularProgressDrawable)
        .error(R.mipmap.ic_img2).signature(ObjectKey(System.currentTimeMillis()))
        .apply(requestOptions)
        .skipMemoryCache(true)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)

}





