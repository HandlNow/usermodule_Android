/*
 * @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.handlService.app.ui.snackBar


interface EventListener {
    /**
     * Called when a   is about to enter the screen
     *
     * @param snackbar the   that's being shown
     */
    fun onShow(snackbar: Snackbar)

    /**
     * Called when a   is about to enter the screen while
     * a   is about to exit the screen by replacement.
     *
     * @param snackbar the   that's being shown
     */
    fun onShowByReplace(snackbar: Snackbar)

    /**
     * Called when a   is fully shown
     *
     * @param snackbar the   that's being shown
     */
    fun onShown(snackbar: Snackbar)

    /**
     * Called when a   is about to exit the screen
     *
     * @param snackbar the   that's being dismissed
     */
    fun onDismiss(snackbar: Snackbar)

    /**
     * Called when a   is about to exit the screen
     * when a new   is about to enter the screen.
     *
     * @param snackbar the   that's being dismissed
     */
    fun onDismissByReplace(snackbar: Snackbar)

    /**
     * Called when a   had just been dismissed
     *
     * @param snackbar the   that's being dismissed
     */
    fun onDismissed(snackbar: Snackbar)
}
