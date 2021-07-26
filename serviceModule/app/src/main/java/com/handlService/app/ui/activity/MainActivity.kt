/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.handlService.app.R
import com.handlService.app.databinding.ActivityMainBinding
import com.handlService.app.model.DrawerData
import com.handlService.app.ui.adapter.DrawerAdapter
import com.handlService.app.ui.extensions.loadFromUrl
import com.handlService.app.ui.fragment.*
import com.handlService.app.ui.fragment.login.*
import com.handlService.app.utils.Const
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*


class MainActivity : BaseActivity(), BaseActivity.PermCallback, AdapterView.OnItemClickListener {
    private var toggle: ActionBarDrawerToggle? = null
    private val drawerItems = ArrayList<DrawerData>()
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initToolbar()
        init()
        initDrawer()
        if (intent != null && intent.hasExtra("detail")) {
            checkAction(intent.getBundleExtra("detail") as Bundle)
        } else {
            callFragments()
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbarTB)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun init() {

        binding!!.headLL.requestLayout()
        binding!!.headLL.setOnClickListener(this)
        toggle = object : ActionBarDrawerToggle(
            this, binding!!.drawer, null,
            R.string.app_name, R.string.app_name
        ) {

            @SuppressLint("RestrictedApi")
            override fun onDrawerOpened(drawerView: View) {
                invalidateOptionsMenu()
            }

            @SuppressLint("RestrictedApi")
            override fun onDrawerClosed(drawerView: View) {
                invalidateOptionsMenu()
            }

        }
        binding!!.drawer.addDrawerListener(toggle!!)
        updateDrawer()

        binding!!.profilePicCIV.setOnClickListener(this)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle!!.syncState()
    }


    override fun updateDrawer() {
        val data = getProfileData()
        binding!!.nameTV.text = data!!.fullName
        binding!!.profilePicCIV.loadFromUrl(this, data.profileFile)
        binding!!.ratingTV.text = data.rating.toString()

    }

    private fun initDrawer() {
        val names = resources.getStringArray(R.array.drawer_new_items)
        val icons = resources.obtainTypedArray(R.array.drawer_new_icons)
        for (i in 0 until icons.length()) {
            val drawerData = DrawerData()
            drawerData.icon = icons.getResourceId(i, -1)
            drawerData.name = names[i]
            drawerItems.add(drawerData)
        }
        icons.recycle()
        val drawerAdapter = DrawerAdapter(this, 0, drawerItems)
        binding!!.drawerLV.adapter = drawerAdapter
        binding!!.drawerLV.onItemClickListener = this

    }

    override fun permGranted(resultCode: Int) {
        showToastOne(getString(R.string.after_permission))
    }

    override fun permDenied(resultCode: Int) {

    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.headLL -> {
                binding!!.drawer.closeDrawers()
            }
        }
    }


    override fun setToolbar(title: String?, screenBg: Int, showTitle: Boolean) {
        if (showTitle) {
            appbarTB.visibility = View.VISIBLE
            titleTV.text = title
            val fragment = supportFragmentManager.findFragmentById(R.id.container)
            binding!!.backIV.visibility = View.GONE

            if (fragment is HomeFragment
                || fragment is MyBusinessSettingsFragment
                || fragment is MyCalendarFragment
                || fragment is MyAppointmentFragment
                || fragment is InsightsFragment
                || fragment is HelpFragment
                || fragment is LegalFragment
                || fragment is ChatListFragment
                || fragment is PromosFragment
            ) {
                binding!!.backIV.visibility = View.VISIBLE
                toggle!!.isDrawerIndicatorEnabled = false
                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                drawer!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                toggle!!.isDrawerIndicatorEnabled = false
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                drawer!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                supportActionBar!!.setHomeAsUpIndicator(R.mipmap.ic_back_arw)
            }
            binding!!.rootRL.setBackgroundColor(screenBg)
        } else {
            appbarTB.visibility = View.GONE
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home && toggle!!.isDrawerIndicatorEnabled) {
            toggle()
            return true
        }

        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)

    }

    fun openDrawer() {
        drawer!!.openDrawer(GravityCompat.END)
    }

    internal fun toggle() {
        val drawerLockMode = drawer!!.getDrawerLockMode(GravityCompat.END)
        if (drawer!!.isDrawerVisible(GravityCompat.END) && drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_OPEN) {
            drawer!!.closeDrawer(GravityCompat.END)
        } else if (drawerLockMode != DrawerLayout.LOCK_MODE_LOCKED_CLOSED) {
            drawer!!.openDrawer(GravityCompat.END)
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        when {
            drawer!!.isDrawerOpen(GravityCompat.END) -> {
                drawer!!.closeDrawer(GravityCompat.END)
            }
            fragment is OrderSummaryFragment -> gotoHomeFragment()
            fragment is HomeFragment
                    || fragment is MyCalendarFragment
                    || fragment is MyBusinessSettingsFragment
                    || fragment is LegalFragment
                    || fragment is VerifiedFragment
                    || fragment is VerificationFragment
                    || fragment is MyAppointmentFragment
                    || fragment is AddPaymentFragment
                    || fragment is InsightsFragment
                    || fragment is ChatListFragment
                    || fragment is HelpFragment
                    || fragment is PromosFragment ->
                backAction()
            supportFragmentManager.backStackEntryCount > 0 -> supportFragmentManager.popBackStack()
            else -> {
                callFragments()
            }
        }
    }

    private fun callFragments() {
        if (getProfileData()!!.isAccount) {
            if (getProfileData()!!.isLocation && getProfileData()!!.isService && getProfileData()!!.isSubscription && getProfileData()!!.isAvailable && getProfileData()!!.isLanguage) {
                gotoHomeFragment()
            } else if (getProfileData()!!.isLocation || getProfileData()!!.isService || getProfileData()!!.isSubscription || getProfileData()!!.isAvailable || getProfileData()!!.isLanguage) {
                if (getProfileData()!!.isImageVerify == Const.IS_VERIFIED && getProfileData()!!.isProofVerify == Const.IS_VERIFIED) {
                    gotoLocationFragment()
                } else {
                    gotoVerifyFragment()
                }
            } else {
                gotoVerifyFragment()
            }
        } else {
            gotoPaymentFragment()
        }

    }

    private fun gotoHomeFragment() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, HomeFragment())
            .commit()
    }

    private fun gotoVerifyFragment() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, VerificationFragment())
            .commit()
    }

    private fun gotoPaymentFragment() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, AddPaymentFragment())
            .commit()
    }

    private fun gotoLocationFragment() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, VerifiedFragment())
            .commit()
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var frag: Fragment? = null
        when (p2) {
            0 -> {
                frag = HomeFragment()

            }
            1 -> {
                frag = MyAppointmentFragment()
            }

            2 -> {
                frag = MyCalendarFragment()
            }
            3 -> {
                frag = MyBusinessSettingsFragment()
            }

            4 -> {
                frag = InsightsFragment()
            }
            5 -> {
                frag = ChatListFragment()

            }
            6 -> {
                frag = PromosFragment()
            }
            7 -> {
                frag = LegalFragment()
                showDataDialog(resources.getStringArray(R.array.select_item), frag)
            }
            8 -> {
                logOut()
            }

        }
        if (p2 != 7) {
            binding!!.drawer.closeDrawers()
            if (frag != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, frag)
                    .commit()
            }
        }
    }

    private fun showDataDialog(stringArray: Array<String>, frag: Fragment) {
        val bundle = Bundle()
        val builderSingle = AlertDialog.Builder(this, R.style.animateDialog)
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, stringArray)
        builderSingle.setAdapter(arrayAdapter) { dialog, which ->
            dialog.dismiss()
            when (which) {
                Const.HELP -> {
                    binding!!.drawer.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, HelpFragment())
                        .commit()
                }
                1 -> {
                    bundle.putInt("type", Const.TERMS)
                    frag.arguments = bundle
                    binding!!.drawer.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, frag)
                        .commit()
                }
                2 -> {
                    bundle.putInt("type", Const.PRIVACY)
                    frag.arguments = bundle
                    binding!!.drawer.closeDrawers()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, frag)
                        .commit()

                }
            }
        }
        builderSingle.show()
    }


    private fun checkAction(bundle: Bundle) {
        if (restFullClient!!.getLoginStatus() != null) {
            val action = bundle.get("action") as String
            val controller = bundle.getString("controller") as String
            val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
            val jsonObj = JSONObject(bundle.getString("detail")!!)
            val orderId = jsonObj.getInt("model_id")

            when (controller) {
                "chat-message" -> {
                    when (action) {

                        "send" -> {
                            val to_user_id = jsonObj.getString("created_by_id")
                            val from_name = jsonObj.getString("created_by_name")
                            if (currentFragment is ChatFragment) {
                                clearNotification()
                            } else {
                                val dataBundle = Bundle()
                                dataBundle.putString("to_id", to_user_id)
                                dataBundle.putString("to_name", from_name)
                                dataBundle.putString("model_id", orderId.toString())
                                val frag = ChatFragment()
                                frag.arguments = dataBundle
                                supportFragmentManager.popBackStack(
                                    null,
                                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                                )
                                supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.container, frag)
                                    .commit()
                            }
                        }


                        else -> {
                            callFragments()

                        }
                    }

                }

                "availability" -> {
                    when (action) {

                        "change-state", "booking" -> {
                            val frag = MyAppointmentFragment()
                            supportFragmentManager.popBackStack(
                                null,
                                FragmentManager.POP_BACK_STACK_INCLUSIVE
                            )
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.container, frag)
                                .commit()

                        }
                        "cancel-booking" -> {
                            clearNotification()
                        }

                        else -> {
                            gotoHomeFragment()

                        }
                    }

                }


                else -> {
                    callFragments()
                }
            }
            clearNotification()
        } else {
            gotoLoginSignUpActivity()
        }
    }


}
