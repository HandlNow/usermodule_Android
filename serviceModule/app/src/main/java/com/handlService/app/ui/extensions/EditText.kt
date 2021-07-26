/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.extensions

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import com.google.i18n.phonenumbers.PhoneNumberUtil


fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })


}

fun AppCompatEditText.makeEditScrollableInsideScrollView() {
    movementMethod = ScrollingMovementMethod()
    setOnTouchListener { v, event ->
        v.parent.requestDisallowInterceptTouchEvent(true)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_UP -> {
                v.parent.requestDisallowInterceptTouchEvent(false)
                //It is required to call performClick() in onTouch event.
                performClick()
            }
        }
        false
    }
}


fun EditText.isBlank(): Boolean {
    return this.text.toString().trim().isEmpty()
}

fun setPhoneNumberLength(mobileET: AppCompatEditText, countryCode: String) {
    val phoneNumberUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
    val isoCode: String = phoneNumberUtil.getRegionCodeForCountryCode(countryCode.toInt())
    val exampleNumber: String = java.lang.String.valueOf(phoneNumberUtil.getExampleNumber(isoCode).getNationalNumber())
    val phoneLength = exampleNumber.length
    mobileET.filters = arrayOf<InputFilter>(
            InputFilter.LengthFilter(phoneLength))
}

val EditText.maxLength: Int?
    get() = filters.filterIsInstance<InputFilter.LengthFilter>().firstOrNull()?.max

fun TextView.checkString(): String {
    return this.text.toString().trim()
}

fun EditText.getLength(): Int {
    return this.text.toString().trim().length
}

fun EditText.checkString(): String {
    return this.text.toString().trim()
}
