package kr.easw.estrader.android.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import kr.easw.estrader.android.R


class MainDialog(context: Context) : Dialog(context) {
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