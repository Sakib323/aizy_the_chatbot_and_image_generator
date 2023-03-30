package com.itsolution.fetchdatafromapp;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;


import com.google.android.material.textfield.TextInputEditText;

import java.nio.charset.Charset;
import java.sql.Time;
import java.util.Random;

public class MyAccessibilityService extends AccessibilityService {
    public boolean encounter;



    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        //performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }
    @Override
    public void onInterrupt() {

    }



    @TargetApi(Build.VERSION_CODES.N)
    public int touchTo(int x, int y) {
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x, y);
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 0, 50));
        boolean click=dispatchGesture(gestureBuilder.build(), null , null);
        if(click==true){
            return 1;
        }else
        {
            return 0;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public int swipe(int dir){

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int middleYValue = displayMetrics.heightPixels / 2;
        final int leftSideOfScreen = displayMetrics.widthPixels / 4;
        final int rightSizeOfScreen = leftSideOfScreen * 3;

        final int height = displayMetrics.heightPixels;
        final int top = (int) (height * .25);
        final int bottom = (int) (height * .75);
        final int midX = displayMetrics.widthPixels / 2;


        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        Path path = new Path();

        if(dir==1)
        {
            //down
            path.moveTo(midX, bottom);
            path.lineTo(midX, top);
            if(encounter==true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(dir==2)
        {
            //up
            path.moveTo(midX, top);
            path.lineTo(midX, bottom);

            if(encounter==true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (dir==3)
        {
            //Swipe left
            path.moveTo(rightSizeOfScreen, middleYValue);
            path.lineTo(leftSideOfScreen, middleYValue);

            if(encounter==true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(dir==4) {
            //Swipe right
            path.moveTo(leftSideOfScreen, middleYValue);
            path.lineTo(rightSizeOfScreen, middleYValue);

            if(encounter==true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if(dir==0){

        }
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(path, 100, 50));
        dispatchGesture(gestureBuilder.build(),null, null);
        return middleYValue;
    }


    //saving macro






}
