package kr.easw.estrader.android.dialog

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R

class RealtorDialog : AppCompatActivity() {
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_realtorwait)

        mHandler = Handler(Looper.getMainLooper())

        // 5초 뒤 RealtorMatchDialog 이동
        mHandler.postDelayed({
            startActivity(
                Intent(this, RealtorMatchDialog::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            )
            finish()
        }, 5000)

    }

    // Runnable, Handler 객체 모두 메모리 누수 유발 방지
    override fun onDestroy() {
        super.onDestroy()
        // Handler 모든 콜백 및 메시지 제거
        mHandler.removeCallbacksAndMessages(null)
        //  이전 Handler 객체와 연결을 끊고 새로운 Looper 로 새 Handler 객체 초기화
        mHandler = Handler(Looper.getMainLooper())
    }
}

