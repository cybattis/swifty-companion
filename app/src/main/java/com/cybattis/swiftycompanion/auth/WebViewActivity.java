package com.cybattis.swiftycompanion.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.cybattis.swiftycompanion.BuildConfig;
import com.cybattis.swiftycompanion.R;

public class WebViewActivity extends AppCompatActivity {
    private static final String TAG  = "WebViewActivity";
    private final String url42 = "https://api.intra.42.fr/oauth/authorize" +
                                 "?client_id=" + BuildConfig.APP_UID +
                                 "&redirect_uri=" + Uri.encode(BuildConfig.REDIRECT_URL) +
                                 "&response_type=code" +
                                 "&scope=public" +
                                 "&state="+ BuildConfig.AUTH_URL_STATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        WebView myWebView = findViewById(R.id.webview);
        myWebView.loadUrl(url42);
        myWebView.clearCache(true);
        myWebView.setWebViewClient(new MyWebViewClient());
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.d(TAG, "shouldOverrideUrlLoading: " + request.getUrl().getHost());

            if ("www.swifty-companion".equals(request.getUrl().getHost())) {
                String code = request.getUrl().getQueryParameter("code");
                if (code == null || code.isEmpty())
                    setResult(Activity.RESULT_CANCELED);
                else {
                    Intent intentData = getIntent().putExtra("code", code);
                    setResult(Activity.RESULT_OK, intentData);
                }
                finish();
                return true;
            }
            return false;
        }
    }
}