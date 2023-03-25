package kr.easw.estrader.android.dialog

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity

/**
 * 대리인 위임 완료 Activity
 * 확인 누르면 전 화면으로 돌아간다는 팝업 뜨고
 * MainListActivity로 이동
 */
class SuccessfulelegationDialog : AppCompatActivity() {
    private lateinit var alertBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //현재 액티비티 레이아웃 fragment_awaitingbid로 설정
        setContentView(R.layout.fragment_successfuldelegation)

        alertBtn = findViewById(R.id.confirm_button)

        // alertBtn 클릭 이벤트
        alertBtn.setOnClickListener {
            // AlertDialog.Builder 클래스의 생성자를 호출해 다이얼로그를 생성
            val builder = AlertDialog.Builder(this,R.style.AppTheme_AlertDialogTheme)
            // 다이얼로그 메시지 및 Positive Button 클릭 이벤트
            builder.setMessage("전 화면으로 돌아갑니다.")
            builder.setPositiveButton("확인") { _, _ ->
                accept() //accept 메소드 호출
            }
            builder.show()
        }
    }

    //accept 메소드 정의
    private fun accept() {
        // MainListActivity로 이동
        val intent = Intent(applicationContext, MainListActivity::class.java)
        startActivity(intent)
    }
}