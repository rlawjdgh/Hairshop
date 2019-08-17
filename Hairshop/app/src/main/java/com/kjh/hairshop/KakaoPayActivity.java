package com.kjh.hairshop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import util.IpInfo;
import util.Tag;

public class KakaoPayActivity extends AppCompatActivity {

    WebView webView;

    int login_idx, staff_idx, price;
    String regdate, time, surgery_name;

    private final String APP_SCHEME = "iamportkakao://";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_pay);

        webView = findViewById(R.id.webView_kakao);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences( KakaoPayActivity.this );
        login_idx = pref.getInt("login_idx", 0);

        Intent intent = getIntent();
        staff_idx = intent.getIntExtra("staff_idx", 0);
        regdate = intent.getStringExtra("regdate");
        time = intent.getStringExtra("time");
        surgery_name = intent.getStringExtra("surgery_name");
        price = intent.getIntExtra("price", 0);

        webView.setWebViewClient(new KakaoWebViewClient(this));
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl(IpInfo.SERVERIP + "kakaoPay.do?login_idx=" + login_idx + "&staff_idx=" + staff_idx + "&regdate=" + regdate +
                "&time=" + time + "&surgery_name=" + surgery_name + "&price=" + price);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if ( intent != null ) {
            Uri intentData = intent.getData();

            if ( intentData != null ) {
                //카카오페이 인증 후 복귀했을 때 결제 후속조치
                String url = intentData.toString();

                if ( url.startsWith(APP_SCHEME) ) {
                    String path = url.substring(APP_SCHEME.length());
                    if ( "process".equalsIgnoreCase(path) ) {
                        webView.loadUrl("javascript:IMP.communicate({result:'process'})");
                    } else {
                        webView.loadUrl("javascript:IMP.communicate({result:'cancel'})");
                    }
                }
            }
        }

    }

}
