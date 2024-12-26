package com.example.news;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class ArticleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        WebView webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient()); // Открытие ссылок внутри WebView

        // Получаем URL из Intent
        String url = getIntent().getStringExtra("article_url");
        if (url != null) {
            webView.loadUrl(url); // Загружаем статью
        }
    }
}
