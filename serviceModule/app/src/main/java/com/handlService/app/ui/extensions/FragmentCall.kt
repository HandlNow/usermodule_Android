/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.extensions

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.handlService.app.R


fun AppCompatActivity.replaceFragment(fragment: Fragment) {
    supportFragmentManager.inTransaction {
        replace(R.id.container, fragment).addToBackStack(fragment.javaClass.name)
    }
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, args: Bundle) {
    fragment.arguments = args
    supportFragmentManager.inTransaction {
        replace(R.id.container, fragment).addToBackStack(fragment.javaClass.name)
    }
}

fun AppCompatActivity.replaceFragmentWithoutStack(fragment: Fragment, args: Bundle) {
    fragment.arguments = args
    supportFragmentManager.inTransaction {
        replace(R.id.container, fragment)
    }
}

fun AppCompatActivity.replaceFragmentWithoutStackWithoutBundle(fragment: Fragment) {
    supportFragmentManager.inTransaction {
        replace(R.id.container, fragment)
    }
}

fun AppCompatActivity.replaceFragWithArgs(fragment: Fragment, frameId: Int, args: Bundle) {
    fragment.arguments = args
    supportFragmentManager.inTransaction {
        replace(frameId, fragment).addToBackStack(fragment.javaClass.name)
    }
}

fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, backStackTag: String? = null) {
    supportFragmentManager.inTransaction {
        add(frameId, fragment)
        backStackTag?.let {
            addToBackStack(fragment.javaClass.name)
        }!!
    }
}