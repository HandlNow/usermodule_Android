/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.handlService.app.R
import com.handlService.app.utils.Const
import kotlinx.android.synthetic.main.activity_splash.*


class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.insetsController!!.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            centerLL.visibility = View.GONE
            secondLL.visibility = View.VISIBLE
            handler.postDelayed({ initFCM() }, Const.SPLASH_TIMEOUT.toLong())
        }, Const.SPLASH_TIMEOUT.toLong())
    }

}
