package com.agongym.store.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.agongym.store.fragments.ShippingAddressFragment;
import com.example.agongym.R;

public class CheckoutActivity extends AppCompatActivity {

    FragmentTransaction transaction;
    Fragment mailingAddressFragment;
    Bundle bundle;
    Cursor cursor;
    String checkoutId = "";
    String token = "";

    int i=0;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //getFragmentManager().beginTransaction().remove(mailingAddressFragment);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        i=0;

        Log.e("vuelve por","ONRESUME");

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}