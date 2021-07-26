/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.snackBar

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import com.handlService.app.BuildConfig

import java.lang.ref.WeakReference

/**
 * A handler for multiple [Snackbar]s
 */
class SnackbarManager private constructor() {

    /**
     * Displays a  in the current [Activity], dismissing
     * the current Snackbar being displayed, if any. Note that the Activity will be obtained from
     * the Snackbar's [android.content.Context]. If the Snackbar was created with
     * [Activity.getApplicationContext] then you must explicitly pass the target
     * Activity using [.show]
     *
     *
     */
    fun show() {
        try {
            show(SnackbarManager.snackBar!!, SnackbarManager.snackBar!!.context as Activity)
        } catch (e: ClassCastException) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Couldn't get Activity from the Snackbar's Context. Try calling " + "#show(Snackbar, Activity) instead", e)
            }
        }

    }

    companion object {

        private val TAG = SnackbarManager::class.java.simpleName
        private val MAIN_THREAD = Handler(Looper.getMainLooper())

        private var snackbarReference: WeakReference<Snackbar>? = null
        private var snackBar: Snackbar? = null
        private var snackBarManager: SnackbarManager? = null


        internal val instance: SnackbarManager
            get() {
                if (snackBarManager == null) {
                    snackBarManager = SnackbarManager()
                }
                return snackBarManager!!
            }

        fun create(snackbar: Snackbar): SnackbarManager? {
            snackBarManager = instance
            SnackbarManager.snackBar = snackbar
            return snackBarManager
        }

        /**
         * Displays a  in the current [Activity], dismissing
         * the current Snackbar being displayed, if any
         *
         * @param snackbar instance of  to display
         * @param activity target [Activity] to display the Snackbar
         */
        fun show(snackbar: Snackbar, activity: Activity) {
            MAIN_THREAD.post(Runnable {
                val currentSnackbar = currentSnackbar
                if (currentSnackbar != null) {
                    if (currentSnackbar.isShowing && !currentSnackbar.isDimissing) {
                        currentSnackbar.dismissAnimation(false)
                        currentSnackbar.dismissByReplace()
                        snackbarReference = WeakReference(snackbar)
                        snackbar.showAnimation(false)
                        snackbar.showByReplace(activity)
                        return@Runnable
                    }
                    currentSnackbar.dismiss()
                }
                snackbarReference = WeakReference(snackbar)
                snackbar.show(activity)
            })
        }

        /**
         * Displays a   in the specified [ViewGroup], dismissing
         * the current Snackbar being displayed, if any
         *
         * @param snackbar       instance of   to display
         * @param parent         parent [ViewGroup] to display the Snackbar
         * @param usePhoneLayout true: use phone layout, false: use tablet layout
         */
        @JvmOverloads
        fun show(snackbar: Snackbar, parent: ViewGroup,
                 usePhoneLayout: Boolean = Snackbar.shouldUsePhoneLayout(snackbar.context)) {
            MAIN_THREAD.post(Runnable {
                val currentSnackbar = currentSnackbar
                if (currentSnackbar != null) {
                    if (currentSnackbar.isShowing && !currentSnackbar.isDimissing) {
                        currentSnackbar.dismissAnimation(false)
                        currentSnackbar.dismissByReplace()
                        snackbarReference = WeakReference(snackbar)
                        snackbar.showAnimation(false)
                        snackbar.showByReplace(parent, usePhoneLayout)
                        return@Runnable
                    }
                    currentSnackbar.dismiss()
                }
                snackbarReference = WeakReference(snackbar)
                snackbar.show(parent, usePhoneLayout)
            })
        }

        /**
         * Dismisses the   shown by this manager.
         */
        fun dismiss() {
            val currentSnackbar = currentSnackbar
            if (currentSnackbar != null) {
                MAIN_THREAD.post { currentSnackbar.dismiss() }
            }
        }

        /**
         * Return the current Snackbar
         */
        val currentSnackbar: Snackbar?
            get() = if (snackbarReference != null) {
                snackbarReference!!.get()
            } else {
                null
            }
    }
}
/**
 * Displays a   in the specified [ViewGroup], dismissing
 * the current Snackbar being displayed, if any
 *
 * @param snackbar instance of   to display
 * @param parent   parent [ViewGroup] to display the Snackbar
 */
