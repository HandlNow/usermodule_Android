/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.socialLogin

import com.facebook.GraphResponse
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

import org.json.JSONObject

class SocialLogin {

    private var listener: SocialLoginListener? = null


    fun setListener(listener: SocialLoginListener) {
        this.listener = listener
    }

    fun facebookLoginDone(`object`: JSONObject, response: GraphResponse) {
        if (listener != null)
            listener!!.onFacebookLoginDone(`object`, response)
    }

    fun gPlusLoginDone(currentPerson: GoogleSignInAccount) {
        if (listener != null)
            listener!!.onGPlusLoginDone(currentPerson)
    }

    interface SocialLoginListener {
        fun onFacebookLoginDone(`object`: JSONObject, response: GraphResponse)

        fun onGPlusLoginDone(currentPerson: GoogleSignInAccount)
    }

    companion object {
        val instance = SocialLogin()
    }
}
