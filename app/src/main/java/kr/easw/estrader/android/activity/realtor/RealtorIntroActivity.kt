package kr.easw.estrader.android.activity.realtor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.extensions.startActivity

class RealtorIntroActivity : AppCompatActivity() {
    private lateinit var mHandler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realtor_intro)

        mHandler = Handler(Looper.getMainLooper())

        mHandler.postDelayed({
            finish()
            startActivity<RealtorMainActivity> {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }, 3000L)
    }

    // Runnable, Handler 객체 모두 메모리 누수 유발 방지
    override fun onDestroy() {
        super.onDestroy()
        // Handler 모든 콜백 및 메시지 제거
        mHandler.removeCallbacksAndMessages(null)
    }
}