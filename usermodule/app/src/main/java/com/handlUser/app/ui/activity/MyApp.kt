/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */
package com.handlUser.app.ui.activity

import android.app.Application
import com.handlUser.app.utils.TypefaceUtil

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        //  This FontsOverride comes from the example I posted above
        TypefaceUtil.overrideFont(applicationContext, "SERIF", "fonts/lato_regular.ttf")
    }
}