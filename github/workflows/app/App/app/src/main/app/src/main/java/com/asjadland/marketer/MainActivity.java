package com.asjadland.marketer;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.os.Build;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private SharedPreferences prefs;
    private static final String PREF_NAME = "MarketerPrefs";
    private static final String KEY_URL = "base_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String savedUrl = prefs.getString(KEY_URL, null);

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        CookieManager.getInstance().setAcceptCookie(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient());

        if (savedUrl != null && !savedUrl.isEmpty()) {
            webView.loadUrl(savedUrl);
        } else {
            showUrlDialog();
        }
    }

    private void showUrlDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("أدخل رابط الخادم");
        builder.setMessage("أدخل عنوان الخادم (مثل http://192.168.1.100:5000/marketer_dashboard)");

        final EditText input = new EditText(this);
        input.setHint("http://10.0.2.2:5000/marketer_dashboard");
        builder.setView(input);

        builder.setPositiveButton("حفظ وتشغيل", (dialog, which) -> {
            String url = input.getText().toString().trim();
            if (!url.isEmpty()) {
                prefs.edit().putString(KEY_URL, url).apply();
                webView.loadUrl(url);
            } else {
                Toast.makeText(MainActivity.this, "الرجاء إدخال رابط صحيح", Toast.LENGTH_SHORT).show();
                showUrlDialog();
            }
        });
        builder.setNegativeButton("إلغاء", (dialog, which) -> finish());
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
