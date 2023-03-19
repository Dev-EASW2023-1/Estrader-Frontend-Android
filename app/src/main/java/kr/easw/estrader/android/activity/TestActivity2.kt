package kr.easw.estrader.android.activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kr.easw.estrader.android.R

class TestActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test2)

        val imageButton = findViewById<Button>(R.id.confirm_button)
        imageButton.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.fragment_dialog)
            dialog.window?.setBackgroundDrawableResource(R.drawable.round_dialog_background)
            dialog.show()
        }}
    fun onPositiveButtonClick(view: View) {
        // Positive Button 클릭 시 처리할 작업 구현
        val intent = Intent(applicationContext, TestActivity2::class.java)
        startActivity(intent)
    }

    fun onNegativeButtonClick(view: View) {
        // Negative Button 클릭 시 처리할 작업 구현
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)

    }
}

