package com.android.ffbf.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.ffbf.R;

import java.util.List;

public class WebActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog.show();

    }

    @Override
    protected int getView() {
        return R.layout.activity_web;
    }

    @Override
    protected void initViews() {
        webView = findViewById(R.id.webView);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.hide();
            }
        });

        webView.loadUrl(getIntent().getStringExtra("webUrl"));
    }

    @Override
    public void onSuccess(List list) {

    }

    @Override
    public void onFailure(String message) {

    }
}
