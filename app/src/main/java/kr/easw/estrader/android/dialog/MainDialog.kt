package kr.easw.estrader.android.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity
import kr.easw.estrader.android.activity.TestActivity2


class MainDialog(context: Context) : Dialog(context) {

    // 이 생성자는 부모 클래스의 생성자를 호출합니다.

    // Dialog를 설정하는 코드 등을 추가합니다.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_dialog)

        // 다이얼로그의 버튼 등을 초기화합니다.
        val okButton = findViewById<Button>(R.id.positive_button)
        okButton.setOnClickListener {
            val intent = Intent(context, AwaitingbidDialog::class.java)
            context.startActivity(intent)
        }

        val cancelButton = findViewById<Button>(R.id.negative_button)
        cancelButton.setOnClickListener {


        }
    }
}