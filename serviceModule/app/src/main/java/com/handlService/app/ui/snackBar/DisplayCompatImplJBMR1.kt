/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.snackBar

import android.annotation.TargetApi
import android.graphics.Point
import android.os.Build
import android.view.Display

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
internal class DisplayCompatImplJBMR1 : DisplayCompat.Impl() {
    override fun getSize(display: Display, outSize: Point) {
        display.getSize(outSize)
    }

    override fun getRealSize(display: Display, outSize: Point) {
        display.getRealSize(outSize)
    }
}
