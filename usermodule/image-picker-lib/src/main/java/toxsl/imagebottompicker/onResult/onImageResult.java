/*
 *  @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package toxsl.imagebottompicker.onResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.theartofdev.edmodo.cropper.CropImage;

import toxsl.imagebottompicker.activity.ProxyActivity;



public class onImageResult {
    public static Builder with(Context context) {
        return new Builder(context);
    }


    public static class Builder {

        private Context context;
        private OnActivityResultListener listener;
        private Intent intent;

        public Builder(Context context) {
            this.context = context;
        }

        public void startActivityForResult() {
            ProxyActivity.startActivityForResult(context, intent, listener);
        }
        public Builder setListener(OnActivityResultListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setIntent(Intent intent) {
            this.intent = intent;
            return this;
        }

    }
}
