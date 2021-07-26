/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlUser.app.ui.fragment.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.facebook.FacebookSdk
import com.facebook.GraphResponse
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.gson.Gson
import com.handlUser.app.BuildConfig
import com.handlUser.app.R
import com.handlUser.app.databinding.FragmentSelectionBinding
import com.handlUser.app.model.ProfileData
import com.handlUser.app.ui.extensions.replaceFragment
import com.handlUser.app.ui.fragment.BaseFragment
import com.handlUser.app.ui.socialLogin.FacebookLogin
import com.handlUser.app.ui.socialLogin.SocialLogin
import com.handlUser.app.utils.ClickHandler
import com.handlUser.app.utils.Const
import com.toxsl.restfulClient.api.Api3Params
import kotlinx.android.synthetic.main.dialog_enter_email.view.*
import org.json.JSONObject


class SelectionFragment : BaseFragment(), ClickHandler, SocialLogin.SocialLoginListener {
    private var socialJson: JSONObject? = null
    private var binding: FragmentSelectionBinding? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private var socialLogin: SocialLogin? = null
    private var profilePicUrl: String? = null
    private var fullName: String? = null
    private var userId: String? = ""
    private var email: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (binding == null)
            binding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_selection, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        baseActivity!!.setToolbar("", ContextCompat.getColor(baseActivity!!, R.color.screen_bg), false)
        binding!!.handleClick = this
    }

    private fun initSocialLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(baseActivity!!, gso)
        FacebookSdk.sdkInitialize(baseActivity)
        socialLogin = SocialLogin.instance
        socialLogin!!.setListener(this)
    }

    override fun onHandleClick(view: View) {
        when (view.id) {
            R.id.loginTV -> baseActivity!!.replaceFragment(LoginFragment())
            R.id.emailTV -> baseActivity!!.replaceFragment(SignupFragment())
            R.id.termsTV -> {
                val bundle = Bundle()
                bundle.putInt("type", Const.TERMS)
                baseActivity!!.replaceFragment(LegalFragment(), bundle)
            }
            R.id.hereTV -> {
                val bundle = Bundle()
                bundle.putInt("type", Const.SEE_MORE)
                baseActivity!!.replaceFragment(LegalFragment(), bundle)
            }
            R.id.policyTV -> {
                val bundle = Bundle()
                bundle.putInt("type", Const.PRIVACY)
                baseActivity!!.replaceFragment(LegalFragment(), bundle)
            }
            R.id.fbIV -> {

                gotoFaceBookLogin()
            }
            R.id.googleIV -> {
                loginWithGoogle()

            }
        }
    }

    private fun gotoFaceBookLogin() {
        startActivity(Intent(baseActivity, FacebookLogin::class.java))
    }

    private fun loginWithGoogle() {
        mGoogleSignInClient?.signOut()
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onFacebookLoginDone(`object`: JSONObject, response: GraphResponse) {
        this.socialJson = `object`
        if (response!!.jsonObject!! != null) {
            try {
                profilePicUrl = if (response.jsonObject!!.has(Const.PICTURE)) {
                    response.jsonObject!!.getJSONObject(Const.PICTURE).getJSONObject(Const.DATA).getString(Const.URL)
                } else {
                    Const.FACEBOOK_LINK + socialJson!!.optString(Const.ID) + Const.PICTURE_TYPE
                }
                fullName = response.jsonObject!!.getString("first_name") + " " + response.jsonObject!!.getString("last_name")
                if (response.jsonObject!!.has("email")) {
                    email = response.jsonObject!!.getString("email")
                }
                val userId = response.jsonObject!!.getString("id")
                val requestParams = Api3Params()
                requestParams.put("User[userId]", userId)
                requestParams.put("User[first_name]", response.jsonObject!!.getString("first_name"))
                requestParams.put("User[last_name]", response.jsonObject!!.getString("last_name"))
                requestParams.put("User[user_name]", "")
                requestParams.put("User[provider]", Const.FACEBOOK)
                requestParams.put("LoginForm[device_token]", baseActivity!!.getDeviceToken())
                requestParams.put("LoginForm[device_name]", Build.MANUFACTURER + "/" + Build.MODEL)
                requestParams.put("LoginForm[device_type]", Const.ANDROID)
                requestParams.put("User[contact_no]", "")
                requestParams.put("img_url", profilePicUrl!!)
                if (email.isNullOrEmpty()) {
                    showEmailDialog(requestParams)
                } else {
                    requestParams.put("User[email]", email!!)
                    hitSocialLoginApi(requestParams)
                }
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onGPlusLoginDone(currentPerson: GoogleSignInAccount) {

    }

    private fun showEmailDialog(requestParams: Api3Params) {
        val dialogBuilder = AlertDialog.Builder(baseActivity!!, R.style.animateDialog)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_enter_email, null)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogView.submitBtn.setOnClickListener {
            when {
                dialogView.emailTIET.text.toString().trim().isEmpty() -> showToastOne(baseActivity!!.getString(R.string.please_enter_email_address))
                !baseActivity!!.isValidMail(binding!!.emailTV.text.toString().trim()) -> showToastOne(getString(R.string.please_enter_a_valid_email_address))
                else -> {
                    alertDialog.dismiss()
                    requestParams.put("User[email]", dialogView.emailTIET.text.toString().trim())
                    hitSocialLoginApi(requestParams)
                }
            }
        }
    }

    private fun hitSocialLoginApi(parmas: Api3Params) {
        val call = api!!.apiSocialLogin(parmas.getServerHashMap())
        restFullClient!!.sendRequest(call, this)

    }

    override fun onSyncSuccess(responseCode: Int, responseMessage: String, responseUrl: String, response: String?) {
        super.onSyncSuccess(responseCode, responseMessage, responseUrl, response)
        try {
            val jsonObjects = JSONObject(response!!)
            if (responseUrl.contains(Const.API_SOCIAL_LOGIN)) {
                if (responseCode == Const.STATUS_OK) {
                    val data = Gson().fromJson<ProfileData>(jsonObjects.getJSONObject("detail").toString(), ProfileData::class.java)
                    showToastOne(getString(R.string.login_sucess))
                    baseActivity!!.setProfileData(data)
                    restFullClient!!.setLoginStatus(data.accessToken)
                    baseActivity!!.gotoMainActivity()
                }
            }
        } catch (e: Exception) {
            baseActivity!!.handleException(e)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            try {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if (result!!.isSuccess) {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = result.signInAccount
                    firebaseAuthWithGoogle(account!!)
                }
            } catch (e: Throwable) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        baseActivity!!.startProgressDialog()
        email = acct.email
        fullName = acct.displayName
        userId = acct.id
        profilePicUrl = if (acct.photoUrl != null) {
            acct.photoUrl.toString()
        } else {
            ""
        }
        val requestParams = Api3Params()
        requestParams.put("User[userId]", userId!!)
        requestParams.put("User[full_name]", fullName!!)
        requestParams.put("User[user_name]", "")
        requestParams.put("User[first_name]", acct.givenName!!)
        requestParams.put("User[last_name]", acct.familyName!!)
        requestParams.put("User[provider]", Const.GOOGLE)
        requestParams.put("LoginForm[device_token]", baseActivity!!.getDeviceToken())
        requestParams.put("LoginForm[device_name]", Build.MANUFACTURER + "/" + Build.MODEL)
        requestParams.put("LoginForm[device_type]", Const.ANDROID)
        requestParams.put("User[contact_no]", "")
        requestParams.put("img_url", profilePicUrl!!)
        if (email.isNullOrEmpty()) {
            showEmailDialog(requestParams)
        } else {
            requestParams.put("User[email]", email!!)
            hitSocialLoginApi(requestParams)
        }
    }

}
