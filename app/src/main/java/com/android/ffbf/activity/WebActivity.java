package com.android.ffbf.activity;


import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.ffbf.R;

import java.util.List;


/**
 * Activity to show WebView of url containing specific Dates selected by user
 */
public class WebActivity extends BaseActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog.show();

    }

    /**
     * Getting View associated with this Activity i.e. xml layout
     */
    @Override
    protected int getView() {
        return R.layout.activity_web;
    }

    /**
     * Initializing Views defined in xml associated with this Activity
     */
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

            /** hide progress dialog when website is ready to view*/
            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.hide();
            }
        });

        webView.loadUrl(getIntent().getStringExtra("webUrl"));
    }

    /**
     * Not Required for this Activity
     */
    @Override
    public void onSuccess(List list) {

    }

    /**
     * Not Required for this Activity
     */
    @Override
    public void onFailure(String message) {

    }
}
