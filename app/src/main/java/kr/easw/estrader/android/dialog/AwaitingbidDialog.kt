package kr.easw.estrader.android.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.TestActivity2

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
            builder.setPositiveButton("확인") { _, _ -> accept()
            }
            // 다이얼로그를 띄워주기
            builder.show()
        }
    }

    private fun accept() {
        // Positive Button 클릭 시 처리할 작업 구현
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.fragment_dialog) // my_dialog_layout은 사용자 정의 대화 상자의 레이아웃 파일 이름입니다.
            val button = dialog.findViewById<Button>(R.id.positive_button) // 레이아웃 파일에서 버튼의 id를 R.id.dialog_button으로 가정합니다.
            val button2 = dialog.findViewById<Button>(R.id.negative_button) // 레이아웃 파일에서 버튼의 id를 R.id.dialog_button으로 가정합니다.

            button.setOnClickListener {
                // 버튼이 클릭되었을 때 실행될 코드를 여기에 작성합니다.
                Toast.makeText(this, "positive_button이 클릭되었습니다!", Toast.LENGTH_SHORT).show()
            }
            button2.setOnClickListener {
                // 버튼이 클릭되었을 때 실행될 코드를 여기에 작성합니다.
                Toast.makeText(this, "negative_button이 클릭되었습니다!", Toast.LENGTH_SHORT).show()
            }

            dialog.show()
        }
    }


