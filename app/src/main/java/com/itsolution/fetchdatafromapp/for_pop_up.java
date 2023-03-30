package com.itsolution.fetchdatafromapp;

import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;

import android.graphics.PixelFormat;
import android.os.Build;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;


class for_pop_up {

    public Context context;
    public View mView;
    public WindowManager.LayoutParams mParams;
    public WindowManager mWindowManager;
    public LayoutInflater layoutInflater;


    public LottieAnimationView loading;

    TextView txt;

    public for_pop_up(Context context){
        this.context=context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);




        mView = layoutInflater.inflate(R.layout.pop_up_view, null);

        txt=mView.findViewById(R.id.text);
        loading=mView.findViewById(R.id.loading);



        mParams.gravity = Gravity.BOTTOM;
        mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);

    }







    public void open() {

        try {
            // check if the view is already
            // inflated or present in the window
            if(mView.getWindowToken()==null) {
                if(mView.getParent()==null) {
                    mWindowManager.addView(mView, mParams);
                }
            }
        } catch (Exception e) {
            Log.d("Error1",e.toString());
        }

    }




    public void close() {

        try {
            // remove the view from the window
            ((WindowManager)context.getSystemService(WINDOW_SERVICE)).removeView(mView);
            // invalidate the view
            mView.invalidate();
            // remove all views
            ((ViewGroup)mView.getParent()).removeAllViews();


            // the above steps are necessary when you are adding and removing
            // the view simultaneously, it might give some exceptions
        } catch (Exception e) {


            Log.e("Error2",e.toString());
        }
    }
}