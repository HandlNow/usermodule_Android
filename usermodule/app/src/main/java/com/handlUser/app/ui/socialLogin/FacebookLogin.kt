/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.socialLogin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.handlUser.app.BuildConfig
import com.handlUser.app.R
import java.util.*


class FacebookLogin : AppCompatActivity() {
    private var callbackmanager: CallbackManager? = null
    private var request: GraphRequest? = null
    private var fbLoginButton: LoginButton? = null
    private var socialLogin: SocialLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_facebook_login)
        socialLogin = SocialLogin.instance
        callbackmanager = CallbackManager.Factory.create()
        fbLoginButton = findViewById<View>(R.id.fbLoginButton) as LoginButton
        fbLoginButton!!.setOnClickListener { initFacebook() }
        fbLoginButton!!.callOnClick()
    }

    fun initFacebook() {
        fbLoginButton!!.setReadPermissions(Arrays.asList("email", "public_profile"))
        LoginManager.getInstance().logOut()
        fbLoginButton!!.registerCallback(callbackmanager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                request = GraphRequest.newMeRequest(loginResult.accessToken) { `object`, response ->
                    socialLogin!!.facebookLoginDone(`object`!!, response!!)
                    finish()
                }
                val parameters = Bundle()
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location")
                request!!.parameters = parameters
                request!!.executeAsync()
            }

            override fun onCancel() {
                log("Facebook login cancel")
                finish()
            }

            override fun onError(error: FacebookException) {
                if (BuildConfig.DEBUG) {
                    error.printStackTrace()
                }
                log("Facebook error :$error")
                finish()
            }
        })
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackmanager!!.onActivityResult(requestCode, resultCode, data)
    }

    private fun log(s: String) {
        if (BuildConfig.DEBUG) {
            Log.e("Facebook Login : >> ", s)
        }
    }
}
