package org.mozilla.fenix.onboarding.view

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun LoginView() {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean {
                        // log click event
                        return super.shouldOverrideUrlLoading(view, request)
                    }
                }

                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(true)


            }
        },
        update = { webView ->
            webView.settings.javaScriptEnabled = true

            webView.loadUrl("https://auth.staging.freespoke.com/realms/freespoke-staging")
        }
    )
}

fun injectJavaScript(webView: WebView, script: String) {
    webView.evaluateJavascript(script, null)
}
