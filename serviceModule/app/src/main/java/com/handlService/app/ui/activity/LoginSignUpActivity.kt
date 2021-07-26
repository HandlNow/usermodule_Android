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
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.handlService.app.R
import com.handlService.app.databinding.ActivityLoginBinding
import com.handlService.app.ui.extensions.replaceFragment
import com.handlService.app.ui.fragment.*
import com.handlService.app.ui.fragment.login.*
import kotlinx.android.synthetic.main.activity_login.*


class LoginSignUpActivity : BaseActivity() {
    private var binding: ActivityLoginBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        initToolbar()
        gotoLangFragment()
    }

    private fun gotoLangFragment() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        replaceFragment(LanguageSelectionFragment())
    }

    private fun gotoLoginFragment() {
        replaceFragment(LoginFragment())
    }

    private fun initToolbar() {
        setSupportActionBar(toolbarTB)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(0)
        supportActionBar!!.setHomeAsUpIndicator(R.mipmap.ic_back_arw)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    override fun setToolbar(title: String?, screenBg: Int, showTitle: Boolean) {
        if (showTitle) {
            binding!!.rootRL.setBackgroundColor(screenBg)
            appbarTB.visibility = View.VISIBLE
            titleTV.text = title
        } else {
            appbarTB.visibility = View.GONE
        }
        binding!!.backIV.visibility = View.GONE
        val fragment = supportFragmentManager.findFragmentById(R.id.container)

        if (fragment is AddPaymentFragment) {
            binding!!.backIV.visibility = View.VISIBLE
            supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        } else {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.mipmap.ic_back_arw)
        }

    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is SelectionFragment || fragment is LanguageSelectionFragment
                || fragment is AddPaymentFragment || fragment is VerificationFragment) {
            backAction()
        } else if (fragment is OTPFragment) {
            supportFragmentManager.popBackStack()
            gotoLoginFragment()
        } else if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            gotoLangFragment()
        }
    }
}
