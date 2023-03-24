package kr.easw.estrader.android.dialog

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity

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


