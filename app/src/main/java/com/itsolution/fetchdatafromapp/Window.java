package com.itsolution.fetchdatafromapp;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Window {

    // declaring required variables
    public Context context;
    public View mView;
    public WindowManager.LayoutParams mParams;
    public WindowManager mWindowManager;
    public LayoutInflater layoutInflater;
    private String apiUrl = "https://api.openai.com/v1/completions";
    private String accessToken = "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";
    public EditText editText;
    public Window(Context context){
        this.context=context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = new WindowManager.LayoutParams(
                    // Shrink the window to wrap the content rather
                    // than filling the screen
                    WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
                    // Display it on top of other application windows
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    // Don't let it grab the input focus
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    // Make the underlying application window visible
                    // through any transparent parts
                    PixelFormat.TRANSLUCENT);
        }

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflating the view with the custom layout we created

        SharedPreferences sharedPref = context.getSharedPreferences("settings", MODE_PRIVATE);

        String wake_up_word =sharedPref.getString("wake_up_word","listen");


        mView = layoutInflater.inflate(R.layout.popup_window, null);

        for_pop_up window=new for_pop_up(context);


        CardView search=mView.findViewById(R.id.search);
        CardView cancel=mView.findViewById(R.id.cancel);
        CardView search_bar=mView.findViewById(R.id.search_bar);
        editText=mView.findViewById(R.id.edit_text);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cancel.getVisibility() == View.GONE ){
                    cancel.setVisibility(View.VISIBLE);
                    search_bar.setVisibility(View.VISIBLE);

                }else{

                    String query=editText.getText().toString();
                    if(!query.isEmpty()){


                        window.txt.setText("THIS IS Empty");

                    }else{

                        window.open();
                        window.loading.setVisibility(View.VISIBLE);
                        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                window.loading.setVisibility(View.GONE);

                                String res=response.toString();


                                JSONArray choicesArray = null;
                                String text="";
                                try {
                                    choicesArray = response.getJSONArray("choices");
                                    JSONObject choiceObject = choicesArray.getJSONObject(0);
                                    text = choiceObject.getString("text");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                                window.txt.setText(text);
                                Log.e("response is ",text);

                            }
                        };

                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("responce is ","response.toString()");
                            }
                        };

                        for_pop_up_view("this is query query", apiUrl, accessToken, responseListener, errorListener);


                    }


                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel.setVisibility(View.GONE);
                search_bar.setVisibility(View.GONE);
                window.close();
            }
        });


        // Define the position of the
        // window within the screen
        mParams.gravity = Gravity.TOP;
        mWindowManager = (WindowManager)context.getSystemService(WINDOW_SERVICE);

    }







    public void for_pop_up_view(String query, String apiUrl, String accessToken, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener) {

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("model", "text-davinci-003");
            requestBody.put("prompt", query);
            requestBody.put("max_tokens", 100);
            requestBody.put("temperature", 1);
            requestBody.put("top_p", 1);
            requestBody.put("frequency_penalty", 0.0);
            requestBody.put("presence_penalty", 0.0);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, apiUrl, requestBody, responseListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + accessToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };
        int timeoutMs = 25000; // 25 seconds timeout
        RetryPolicy policy = new DefaultRetryPolicy(timeoutMs, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        // Add the request to the Volley request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
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

