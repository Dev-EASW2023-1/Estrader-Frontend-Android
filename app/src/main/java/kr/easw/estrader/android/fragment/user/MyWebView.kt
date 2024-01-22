package kr.easw.estrader.android.fragment.user

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SetJavaScriptEnabled", "ViewConstructor")
class MyWebView(context: Context, url: String): WebView(context) {

    init {
        settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
        }

        addJavascriptInterface(object: Any() {
            @JavascriptInterface
            fun getData(data: String) {
                CoroutineScope(Dispatchers.Default).launch {

                    withContext(CoroutineScope(Dispatchers.Main).coroutineContext) {
                    }
                }
            }
        }, "{Name}")

        webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return false // 브라우저를 사용하지 않고, 웹뷰에서 URL을 열도록 한다.
            }
        }

        //webChromeClient 등도 필요에 따라 설정 가능하다.

        loadUrl(url) // 입력받은 url을 웹뷰에서 로드한다.
    }
}