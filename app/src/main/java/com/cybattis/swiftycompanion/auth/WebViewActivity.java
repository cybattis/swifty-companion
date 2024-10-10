package com.cybattis.swiftycompanion.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_web_view);

        WebView myWebView = findViewById(R.id.webview);
        myWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        myWebView.loadUrl(url42);
        myWebView.setWebViewClient(new WebViewActivity.MyWebViewClient());
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());

            Log.d(TAG, "shouldOverrideUrlLoading: " + request.getUrl().getHost());

            if ("www.swifty-companion".equals(request.getUrl().getHost())) {
                String code = request.getUrl().getQueryParameter("code");
                if (code == null || code.isEmpty())
                    setResult(Activity.RESULT_CANCELED);
                else {
                    Intent intentData = getIntent().putExtra("code", code);
                    setResult(Activity.RESULT_OK, intentData);
                }

                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookies(null);

                finish();
                return true;
            }
            return false;
        }
    }
}