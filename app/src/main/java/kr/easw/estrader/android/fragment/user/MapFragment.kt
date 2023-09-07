package kr.easw.estrader.android.fragment.user

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.R

class MapFragment : Fragment() {

    // WebView 인스턴스 변수
    private var webView: WebView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // fragment_map 레이아웃 리소스를 인플레이트하고, rootView로 참조
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        // rootView에서 WebView를 찾아 webView 변수에 할당합니다.
        webView = rootView.findViewById(R.id.web_view)
        webView?.settings?.javaScriptEnabled = true  // WebView에 JavaScript를 활성화

        // WebView 설정 추가
        webView?.settings?.apply {
            // 줌 관련 설정
            setSupportZoom(false)         // 줌을 지원하지 않도록 설정
            builtInZoomControls = false   // 내장 줌 컨트롤을 사용하지 않도록 설정
            displayZoomControls = false   // 줌 컨트롤 표시를 사용하지 않도록 설정

            // 기타 WebView 설정
            domStorageEnabled = true      // DOM 스토리지 활성화
            loadWithOverviewMode = true   // 웹 페이지가 전체 뷰 너비에 로드되도록 설정
            useWideViewPort = true        // 뷰포트 태그 지원 활성화
        }

        webView?.webViewClient = WebViewClient()  // WebView에 기본 WebViewClient를 설정

        // 스프링부트 서버의 엔드포인트 URL 로드
        webView?.loadUrl("http://172.17.0.10:8060/user/navermap")  // 지정된 URL을 WebView에 로드

        return rootView
    }

    // WebView의 뒤로 가기 동작을 처리하기 위한 메소드
    fun canGoBack(): Boolean {
        // WebView가 뒤로 갈 페이지가 있는지 확인
        return webView?.canGoBack() ?: false
    }

    fun goBack() {
        webView?.goBack()
    }
}
