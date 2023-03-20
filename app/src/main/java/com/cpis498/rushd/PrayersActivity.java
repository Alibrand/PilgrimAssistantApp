package com.cpis498.rushd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class PrayersActivity extends AppCompatActivity {

    WebView webview_prayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayers);
        webview_prayers=findViewById(R.id.webview_prayers);

        webview_prayers.loadUrl("file:///android_asset/prayers.html");

    }
}