package kr.easw.estrader.android.dialog

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R

/**
 * 대리위임 신청 대기 Dialog
 *
 * 지금은 5초 뒤, SuccessDelegationDialog 이동
 * 추후 대리인 앱에서 대리위임 수락 후, SuccessDelegationDialog 이동 수정 예정
 */
class RealtorDialog : AppCompatActivity() {
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_realtorwait)

        mHandler = Handler(Looper.getMainLooper())

        // 5초 뒤 SuccessDelegationDialog 이동
        mHandler.postDelayed({
            startActivity(
                Intent(this, RealtormatchDialog::class.java).apply {
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

