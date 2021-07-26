/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.socialLogin

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.handlService.app.R


class GPlusLoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private var mGoogleApiClient: GoogleApiClient? = null
    private var mConnectionResult: ConnectionResult? = null
    private var socialLogin: SocialLogin? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        socialLogin = SocialLogin.instance
        createGoogleClient()
    }

    private fun createGoogleClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()
    }

    public override fun onStop() {
        super.onStop()
        if (mGoogleApiClient!!.isConnected) {
            mGoogleApiClient!!.disconnect()
        }
    }

    private fun resetAndLogin() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { signInWithGplus() }
    }

    private fun signInWithGplus() {
        if (!mGoogleApiClient!!.isConnected) {
            resolveSignInError()
        } else {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, G_PLUS_SIGNIN_REQ_CODE)
        }
    }

    private fun resolveSignInError() {
        if (mConnectionResult != null) {
            if (mConnectionResult!!.hasResolution()) {
                try {
                    mConnectionResult!!.startResolutionForResult(this, RC_SIGN_IN)
                } catch (e: IntentSender.SendIntentException) {
                    mGoogleApiClient!!.connect()
                }

            }
        } else {
            mGoogleApiClient!!.clearDefaultAccountAndReconnect()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == G_PLUS_SIGNIN_REQ_CODE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result!!.isSuccess) {
                val acct = result.signInAccount
                if (acct != null)
                    socialLogin!!.gPlusLoginDone(acct)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
            }
        }
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        this.mConnectionResult = connectionResult
        resolveSignInError()
    }

    override fun onConnected(bundle: Bundle?) {
        resetAndLogin()
    }

    override fun onConnectionSuspended(i: Int) {

    }

    companion object {
        private val RC_SIGN_IN = 123
        private val G_PLUS_SIGNIN_REQ_CODE = 1212
    }
}
