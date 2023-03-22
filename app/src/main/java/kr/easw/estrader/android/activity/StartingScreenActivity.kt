package kr.easw.estrader.android.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.dialog.AwaitingbidDialog

class StartingScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login)
        val imageButton = findViewById<Button>(R.id.btnNext)
        imageButton.setOnClickListener {
            val intent = Intent(applicationContext, AwaitingbidDialog::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}