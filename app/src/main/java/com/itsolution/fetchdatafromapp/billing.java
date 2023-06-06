package com.itsolution.fetchdatafromapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class billing extends AppCompatActivity implements BillingProcessor.IBillingHandler {


    private BillingProcessor bp;
    private PurchaseInfo one_month,three_month,year_pack;
    private String license_key="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqo/Fr0VYV5Rimk6ePEMto0kzrhj8++KXS878tDoKjipznTlunF8tP3P9RgTIObbBkp4z4AbS7jMRUFf9nia230r7xoUUk2sdx3yO1DXTqNe/kWoS815XRALAxgdoKHSnUcZn+0RNmpsl8WLJfMB6WOrDqgseV99c94sOspLrBDlMh3G13686Tb/h3RY3WVZnLProbfBCB5NYvnv6ssgxj2/YvDDTX274tCU1HJKI7Mbs32k51T0fHoYefaVSvwdt6+yEXl9UW5sBTyL7obGgEltlq6/tsOl2Zk+BDrbmsR2GEbza3qItUsHu8xSf4IeoHTzntFALTthaVSo2TEADuwIDAQAB";
    private String one_month_pack="one_month_pack";
    private String three_month_pack="three_month_new";
    private String yr_pack="yearly_pack_new";
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();


        Dialog add_car=new Dialog(billing.this);
        add_car.setContentView(R.layout.subscription_pop_up);
        add_car.getWindow().setBackgroundDrawableResource(R.color.transparent);
        add_car.setCancelable(true);

        CardView dismiss=add_car.findViewById(R.id.dismiss);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_car.dismiss();
            }
        });

        CardView btn=add_car.findViewById(R.id.btn);
        TextView price=add_car.findViewById(R.id.price);
        bp=new BillingProcessor(billing.this,license_key,this);
        bp.initialize();

        CardView one_month=findViewById(R.id.one_month_pack);
        one_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_car.show();
                price.setText("USD 10 / Month");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bp.subscribe(billing.this,one_month_pack);
                    }
                });


            }
        });

        CardView three_month=findViewById(R.id.three_month_pack);
        three_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                add_car.show();

                price.setText("USD 20 / Three Month");

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bp.subscribe(billing.this,three_month_pack);
                    }
                });



            }
        });

        CardView year=findViewById(R.id.yearly);
        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                add_car.show();

                price.setText("USD 60 / One Year");

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bp.subscribe(billing.this,yr_pack);
                    }
                });



            }
        });


    }

    @Override
    public void onProductPurchased(String productId, PurchaseInfo details) {
        SharedPreferences user=getSharedPreferences("user_info",MODE_PRIVATE);
        String phone=user.getString("phone","");



        db= FirebaseDatabase.getInstance().getReference().child("User").child(phone).child("subscription");
        db.setValue(productId);



    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {



        bp.loadOwnedPurchasesFromGoogleAsync(new BillingProcessor.IPurchasesResponseListener() {
            @Override
            public void onPurchasesSuccess() {

            }

            @Override
            public void onPurchasesError() {

            }
        });

        one_month=bp.getSubscriptionPurchaseInfo("one_month_pack");
        three_month=bp.getSubscriptionPurchaseInfo("three_month_new");
        year_pack=bp.getSubscriptionPurchaseInfo("yearly_pack_new");

        if(one_month!=null){
            if(one_month!=null){

                if(one_month.purchaseData.autoRenewing){
                    Toast.makeText(this, "Already Subscribed", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Not Subscribed", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "Expired", Toast.LENGTH_SHORT).show();
            }
        }


    }


    @Override
    protected void onDestroy() {
        if(bp!=null){
            bp.release();
        }

        super.onDestroy();
    }
}