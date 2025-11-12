package com.example.blueguard

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class ForecastMapActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast_map)

        webView = findViewById(R.id.webViewForecast)
        progressBar = findViewById(R.id.progressBar)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        setupWebView()

        webView.loadUrl("https://mosdac.gov.in/rip/")
    }

    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return false
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}