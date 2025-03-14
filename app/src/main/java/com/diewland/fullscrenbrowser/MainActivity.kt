package com.diewland.fullscrenbrowser

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    companion object {
        private const val PREFS_NAME = "FsBrowser"
        private const val KEY_TEXT = "url"
    }
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mediaPlaybackRequiresUserGesture = false

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        val url = loadURL()
        if (url.isEmpty()) {
            promptNewURL()
        } else {
            webView.loadUrl(url)
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    // display
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    // prompt
    private fun promptNewURL() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Please input URL")
            .setMessage("Ex: https://youtube.com")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val newURL = input.text.toString()
                saveURL(newURL)
                webView.loadUrl(newURL)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // local storage
    private fun saveURL(text: String) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_TEXT, text)
        editor.apply()
    }
    private fun loadURL(): String {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_TEXT, "") ?: ""
    }
}