package kr.easw.estrader.android.activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kr.easw.estrader.android.R

class TestActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)

        // accept 버튼 클릭 시 DialogActivity 실행
        val acceptButton = findViewById<Button>(R.id.confirm_button)
        acceptButton.setOnClickListener {


        }
    }
}


