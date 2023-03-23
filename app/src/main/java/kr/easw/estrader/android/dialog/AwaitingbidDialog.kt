package kr.easw.estrader.android.dialog

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity

class AwaitingbidDialog : AppCompatActivity() {
    private lateinit var alertBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_awaitingbid)

        // View inflate
        alertBtn = findViewById(R.id.confirm_button)

        // 기본 형태의 다이얼로그
        alertBtn.setOnClickListener {
            // 다이얼로그를 생성하기 위해 Builder 클래스 생성자를 이용해 줍니다.
            val builder = AlertDialog.Builder(this)
            // 다이얼로그 메시지 설정
            builder.setMessage("전 화면으로 돌아갑니다.")
            // Positive Button 클릭 시 처리할 작업 설정
            builder.setPositiveButton("확인") { _, _ ->
                accept()
            }
            // 다이얼로그를 띄워주기
            builder.show()
        }
    }

    private fun accept() {
        // Positive Button 클릭 시 처리할 작업 구현
        val intent = Intent(applicationContext, MainListActivity::class.java)
        startActivity(intent)
    }
}


