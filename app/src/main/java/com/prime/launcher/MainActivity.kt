package com.prime.launcher

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.pm.ActivityInfo
import android.webkit.JavascriptInterface
import android.widget.Toast
import android.content.Intent
import androidx.core.content.FileProvider
import android.app.AlertDialog
import android.provider.Settings
import android.net.Uri
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.HttpURLConnection
import java.io.File
import java.io.FileOutputStream
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat

class MainActivity : ComponentActivity() {
    private var primeWebView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )

        checkForLauncherUpdate()

        val assetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        setContent {
            BackHandler {
                if (primeWebView?.canGoBack() == true) primeWebView?.goBack() else finish()
            }
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        primeWebView = this
                        setBackgroundColor(android.graphics.Color.rgb(9, 9, 9))
                        settings.javaScriptEnabled = true
                        addJavascriptInterface(AndroidPrimeBridge(), "AndroidPrime")
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = false
                        settings.mediaPlaybackRequiresUserGesture = true
                        settings.allowFileAccess = false
                        settings.allowContentAccess = false
                        isVerticalScrollBarEnabled = false
                        isHorizontalScrollBarEnabled = false
                        webChromeClient = WebChromeClient()
                        webViewClient = object : WebViewClientCompat() {
                            override fun shouldInterceptRequest(
                                view: WebView,
                                request: WebResourceRequest
                            ) = assetLoader.shouldInterceptRequest(request.url)

                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest
                            ): Boolean {
                                return request.url.host != "appassets.androidplatform.net"
                            }
                        }
                        loadUrl("https://appassets.androidplatform.net/assets/prime/index.html")
                    }
                }
            )
        }
    }

    private inner class AndroidPrimeBridge {
        @JavascriptInterface
        fun setBattleMode(active: Boolean) {
            runOnUiThread {
                requestedOrientation = if (active) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                window.decorView.postDelayed({ primeWebView?.evaluateJavascript("window.dispatchEvent(new Event('resize'))", null) }, 350)
            }
        }
    }

    private fun checkForLauncherUpdate() {
        lifecycleScope.launch {
            val release = withContext(Dispatchers.IO) {
                try {
                    val connection = (URL("https://api.github.com/repos/kisia319-cell/PrimeLauncher-Mobile/releases/latest").openConnection() as HttpURLConnection)
                    connection.connectTimeout = 7000
                    connection.readTimeout = 7000
                    connection.setRequestProperty("Accept", "application/vnd.github+json")
                    val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
                    connection.disconnect()
                    val tag = json.optString("tag_name")
                    val remoteCode = tag.removePrefix("mobile-").toIntOrNull() ?: 0
                    val asset = json.optJSONArray("assets")?.let { list ->
                        (0 until list.length()).map { list.getJSONObject(it) }
                            .firstOrNull { it.optString("name") == "PrimeLauncher-Mobile.apk" }
                    }
                    if (remoteCode > BuildConfig.VERSION_CODE && asset != null)
                        Pair(asset.optString("browser_download_url"), tag)
                    else null
                } catch (_: Exception) { null }
            }
            if (release != null && !isFinishing) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Доступно обновление PrimeLauncher")
                    .setMessage("Найдена новая версия ${release.second}. Обновить лаунчер и встроенную игру?")
                    .setNegativeButton("Позже", null)
                    .setPositiveButton("Обновить") { _, _ -> downloadAndInstall(release.first) }
                    .show()
            }
        }
    }

    private fun downloadAndInstall(url: String) {
        lifecycleScope.launch {
            val apk = withContext(Dispatchers.IO) {
                try {
                    val connection = (URL(url).openConnection() as HttpURLConnection)
                    connection.connectTimeout = 15000
                    connection.readTimeout = 30000
                    val file = File(cacheDir, "prime-update.apk")
                    connection.inputStream.use { input -> FileOutputStream(file).use { output -> input.copyTo(output) } }
                    connection.disconnect()
                    file
                } catch (_: Exception) { null }
            }
            if (apk == null) {
                Toast.makeText(this@MainActivity, "Не удалось скачать обновление", Toast.LENGTH_LONG).show()
                return@launch
            }
            try {
                if (android.os.Build.VERSION.SDK_INT >= 26 && !packageManager.canRequestPackageInstalls()) {
                    Toast.makeText(this@MainActivity, "Разреши установку обновлений для PrimeLauncher, затем повтори обновление", Toast.LENGTH_LONG).show()
                    startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName")))
                    return@launch
                }
                val uri = FileProvider.getUriForFile(this@MainActivity, "$packageName.fileprovider", apk)
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            } catch (_: Exception) {
                Toast.makeText(this@MainActivity, "Не удалось открыть установщик Android", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        primeWebView?.destroy()
        primeWebView = null
        super.onDestroy()
    }
}
