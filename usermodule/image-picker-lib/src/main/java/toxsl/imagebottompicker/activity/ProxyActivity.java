/*
 *  @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 * @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *
 * All Rights Reserved.
 * Proprietary and confidential :  All information contained herein is, and remains
 * the property of ToXSL Technologies Pvt. Ltd. and its partners.
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package toxsl.imagebottompicker.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;


import androidx.appcompat.app.AppCompatActivity;

import toxsl.imagebottompicker.model.ActivityRequest;
import toxsl.imagebottompicker.onResult.OnActivityResultListener;

public class ProxyActivity extends AppCompatActivity {
    private static Deque<ActivityRequest> activityRequestStack;


    public static void startActivityForResult(Context context, Intent intent, OnActivityResultListener listener) {

        if (activityRequestStack == null) {
            activityRequestStack = new ArrayDeque<>();
        }

        ActivityRequest activityRequest = new ActivityRequest(intent, listener);
        activityRequestStack.push(activityRequest);

        Intent tempIntent = new Intent(context, ProxyActivity.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        context.startActivity(tempIntent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (activityRequestStack == null) {
            finish();
            return;
        }
        ActivityRequest activityRequest = activityRequestStack.peek();

        Intent intent = activityRequest.getIntent();
        super.startActivityForResult(intent, new Random().nextInt(65536));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityRequest activityRequest = activityRequestStack.pop();
        OnActivityResultListener listener = activityRequest.getListener();

        listener.onActivityResult(resultCode, data);

        if (activityRequestStack.size() == 0) {
            activityRequestStack = null;
        }
        finish();

    }
}

