package com.agongym.store.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

import com.example.agongym.R;

public class PaymentActivity extends AppCompatActivity {

    String webUrl ="";
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        webUrl = getIntent().getExtras().getString("webUrl");
        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(webUrl);







    }
}