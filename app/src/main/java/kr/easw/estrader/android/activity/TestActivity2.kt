package kr.easw.estrader.android.activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import kr.easw.estrader.android.R

class TestActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_agentdelegation)

        // accept 버튼 클릭 시 DialogActivity 실행
        val acceptButton = findViewById<Button>(R.id.confirm_button2)
        acceptButton.setOnClickListener {
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
        val negativeButton = findViewById<Button>(R.id.confirm_button)
        negativeButton.setOnClickListener {
            Toast.makeText(this, "negative_button이 클릭되었습니다!", Toast.LENGTH_SHORT).show()

        }

    }
}


