package kr.easw.estrader.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.airbnb.lottie.LottieAnimationView
import kr.easw.estrader.android.R

class TestActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test1)

        val imageButton = findViewById<Button>(R.id.confirm_button2)
        imageButton.setOnClickListener {
            val intent = Intent(applicationContext, TestActivity2::class.java)
            startActivity(intent)
        }
    }
}