package kr.easw.estrader.android.dialog

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R

/**
 * 대리신청중 화면 Activity
 * 지금은 5초 지연 후 SuccessfulelegationDialog이동이지만
 * 추후에는 대리인 어플에서 수락 하면 이동으로 수정
 */
class AwaitingbidDialog : AppCompatActivity() {
    private lateinit var alertBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //현재 액티비티 레이아웃 fragment_awaitingbid로 설정
        setContentView(R.layout.fragment_awaitingbid)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SuccessfulelegationDialog::class.java)
            startActivity(intent)
            finish()
        }, 5000)

    }

    //accept 메소드 정의

}


