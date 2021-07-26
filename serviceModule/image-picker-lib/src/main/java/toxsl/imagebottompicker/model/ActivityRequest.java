/*
 *  @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package toxsl.imagebottompicker.model;

import android.content.Intent;

import toxsl.imagebottompicker.onResult.OnActivityResultListener;


public class ActivityRequest {

    private Intent intent;
    private OnActivityResultListener listener;

    public ActivityRequest(Intent intent,
                           OnActivityResultListener listener) {
        this.intent = intent;
        this.listener = listener;
    }

    public Intent getIntent() {
        return intent;
    }

    public OnActivityResultListener getListener() {
        return listener;
    }
}
