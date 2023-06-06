package com.itsolution.fetchdatafromapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class register extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    public DatabaseReference databaseReference;
    public SharedPreferences sharedPreferences,sharedPreferences_2;
    public RewardedAd mrewardedAd;
    public String rewarded_ad_id=null;

    public String update_app;
    private BillingProcessor bp;
    private PurchaseInfo one_month,three_month,year_pack;
    private String license_key="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqo/Fr0VYV5Rimk6ePEMto0kzrhj8++KXS878tDoKjipznTlunF8tP3P9RgTIObbBkp4z4AbS7jMRUFf9nia230r7xoUUk2sdx3yO1DXTqNe/kWoS815XRALAxgdoKHSnUcZn+0RNmpsl8WLJfMB6WOrDqgseV99c94sOspLrBDlMh3G13686Tb/h3RY3WVZnLProbfBCB5NYvnv6ssgxj2/YvDDTX274tCU1HJKI7Mbs32k51T0fHoYefaVSvwdt6+yEXl9UW5sBTyL7obGgEltlq6/tsOl2Zk+BDrbmsR2GEbza3qItUsHu8xSf4IeoHTzntFALTthaVSo2TEADuwIDAQAB";

    String TAG ="APP ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar=getSupportActionBar();
        actionBar.hide();




        DatabaseReference app_id=FirebaseDatabase.getInstance().getReference("app_ads").child("app_id");
        app_id.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String app_id_from_db=snapshot.getValue(String.class);
                Log.e(TAG,app_id_from_db);

                try {
                    ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                    Bundle bundle = ai.metaData;
                    String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                    Log.d(TAG, "Name Found: " + myApiKey);
                    ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", app_id_from_db);//you can replace your key APPLICATION_ID here
                    String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                    Log.d(TAG, "ReNamed Found: " + ApiKey);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                } catch (NullPointerException e) {
                    Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });



        databaseReference=FirebaseDatabase.getInstance().getReference();
        sharedPreferences_2=getSharedPreferences("registered",MODE_PRIVATE);

        Boolean logged=sharedPreferences_2.getBoolean("yes",false);

        if(logged==true){


            bp=new BillingProcessor(register.this,license_key,this);
            bp.initialize();

            DatabaseReference native_ads = FirebaseDatabase.getInstance().getReference();


            native_ads.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    SharedPreferences user=getSharedPreferences("user_info",MODE_PRIVATE);
                    String phone=user.getString("phone","");

                    rewarded_ad_id = snapshot.child("app_ads").child("rewarded_video_ad").getValue(String.class);
                    String subscription=snapshot.child("User").child(phone).child("subscription").getValue(String.class);

                    DatabaseReference update=FirebaseDatabase.getInstance().getReference().child("update");
                    update.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            update_app=snapshot.getValue(String.class);


                            if(update_app.contains("yes")){
                                Toast.makeText(register.this, "update the app for further use", Toast.LENGTH_LONG).show();
                                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }else{


                                if(rewarded_ad_id.contains("empty")){
                                    Intent intent =new Intent(register.this,MainActivity.class);
                                    startActivity(intent);
                                }else{
                                    if(subscription.contains("null")){
                                        load_ads();
                                    }
                                    else{
                                        Intent intent =new Intent(register.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });



                    //ca-app-pub-3940256099942544/5224354917

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(register.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
                }
            });

            MobileAds.initialize(register.this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {

                }
            });


        }else {

            CountDownTimer countDownTimer=new CountDownTimer(5000,1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    register();
                }
            }.start();

        }




    }

    private void show_ads(){
        if(mrewardedAd!=null){
            mrewardedAd.show(register.this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(RewardItem rewardItem) {

                    Intent intent =new Intent(register.this,MainActivity.class);
                    startActivity(intent);

                }
            });
        }

    }

    private void load_ads() {
        AdRequest adRequest=new AdRequest.Builder().build();
        RewardedAd.load(register.this, rewarded_ad_id, adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                Intent intent =new Intent(register.this,MainActivity.class);
                startActivity(intent);

                mrewardedAd=null;

            }

            @Override
            public void onAdLoaded(RewardedAd rewardedAd) {
                super.onAdLoaded(rewardedAd);
                mrewardedAd=rewardedAd;
                show_ads();

                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                    }
                });
            }
        });

    }

    private void register(){

        Dialog add_car=new Dialog(register.this);
        add_car.setContentView(R.layout.profile);
        add_car.getWindow().setBackgroundDrawableResource(R.color.transparent);
        add_car.setCancelable(false);
        add_car.show();
        EditText email,pass,phone;

        TextView sign_in=add_car.findViewById(R.id.sign_in);
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_car.dismiss();
                Dialog add_car1=new Dialog(register.this);
                add_car1.setContentView(R.layout.login);
                add_car1.getWindow().setBackgroundDrawableResource(R.color.transparent);
                add_car1.setCancelable(false);
                add_car1.show();

                CardView login=add_car1.findViewById(R.id.login);
                EditText phone,pass;

                TextView sign_up=add_car1.findViewById(R.id.sign_up);
                sign_up.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        add_car1.dismiss();
                        add_car.show();
                    }
                });

                phone=add_car1.findViewById(R.id.phone);
                pass=add_car1.findViewById(R.id.password);

                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String phone_string,pass_string;

                        pass_string=pass.getText().toString();

                        phone_string=phone.getText().toString();
                        LottieAnimationView loading=add_car1.findViewById(R.id.animation_1);


                        if(!pass_string.isEmpty() && !phone_string.isEmpty()){
                            loading.setVisibility(View.VISIBLE);

                            DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("User");

                            Query check=db.orderByChild("phone").equalTo(phone_string);
                            check.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        String passwordFromDB=snapshot.child(phone_string).child("pass").getValue(String.class);

                                        String emailFromDB=snapshot.child(phone_string).child("email").getValue(String.class);

                                        if(passwordFromDB.equals(pass_string)){

                                            sharedPreferences=getSharedPreferences("user_info",MODE_PRIVATE);
                                            SharedPreferences.Editor edit=sharedPreferences.edit();
                                            edit.putString("email",emailFromDB);
                                            edit.putString("phone",phone_string);
                                            edit.putString("pass",passwordFromDB);
                                            edit.apply();

                                            SharedPreferences.Editor editor= sharedPreferences_2.edit();
                                            editor.putBoolean("yes",true);
                                            editor.putBoolean("onstart",true);
                                            editor.apply();
                                            Intent intent =new Intent(register.this,MainActivity.class);
                                            startActivity(intent);


                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {

                                }
                            });




                        }else{

                            Toast.makeText(register.this, "empty field!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });

        email=add_car.findViewById(R.id.email);
        pass=add_car.findViewById(R.id.password);
        phone=add_car.findViewById(R.id.phone);

        LottieAnimationView lottieAnimationView=add_car.findViewById(R.id.animation_1);


        CardView next_btn=add_car.findViewById(R.id.next_btn);
        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                String email_from_text,pass_from_text,phone_from_text;

                email_from_text=email.getText().toString();
                pass_from_text=pass.getText().toString();
                phone_from_text=phone.getText().toString();


                TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                String countryCode = tm.getSimCountryIso();

                HashMap<String,String> hashMap =new HashMap<String,String>();
                hashMap.put("email",email_from_text);
                hashMap.put("phone",phone_from_text);
                hashMap.put("pass",pass_from_text);

                hashMap.put("country",countryCode);



                if(!email_from_text.isEmpty() && !phone_from_text.isEmpty() && !pass_from_text.isEmpty()){
                    DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("User");

                    Query check=db.orderByChild("phone").equalTo(phone_from_text);

                    check.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(register.this, "This phone number already exist", Toast.LENGTH_SHORT).show();

                            }else{


                                lottieAnimationView.setVisibility(View.VISIBLE);
                                sharedPreferences=getSharedPreferences("user_info",MODE_PRIVATE);
                                SharedPreferences.Editor edit=sharedPreferences.edit();
                                edit.putString("email",email_from_text);
                                edit.putString("phone",phone_from_text);
                                edit.putString("pass",pass_from_text);

                                edit.apply();

                                SharedPreferences.Editor editor= sharedPreferences_2.edit();
                                editor.putBoolean("yes",true);
                                editor.putBoolean("onstart",true);
                                editor.apply();


                                Calendar calendar = Calendar.getInstance();
                                Date today = calendar.getTime();
                                //calendar.add(Calendar.DAY_OF_YEAR, 1);
                                //Date tomorrow = calendar.getTime();
                                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
                                String tomorrowAsString = dateFormat.format(today);

                                databaseReference.child("User").child(phone_from_text).setValue(hashMap);
                                databaseReference.child("User").child(phone_from_text).child("expire_date").setValue(tomorrowAsString);
                                databaseReference.child("User").child(phone_from_text).child("subscription").setValue("null");
                                Intent intent =new Intent(register.this,MainActivity.class);
                                startActivity(intent);


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }
                    });

                }else{
                    Toast.makeText(register.this, "empty field!", Toast.LENGTH_SHORT).show();
                }


            }
        });




    }

    @Override
    public void onProductPurchased(String productId, PurchaseInfo details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {


        one_month=bp.getSubscriptionPurchaseInfo("one_month_pack");
        three_month=bp.getSubscriptionPurchaseInfo("three_month_new");
        year_pack=bp.getSubscriptionPurchaseInfo("yearly_pack_new");

        SharedPreferences sharedPreferences=getSharedPreferences("user_info",MODE_PRIVATE);
        String phn=sharedPreferences.getString("phone","");

        if(one_month==null || three_month==null || year_pack==null){

            DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("User").child(phn).child("subscription");
            db.setValue("null");


        }


    }
}