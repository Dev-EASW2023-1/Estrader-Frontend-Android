package kr.easw.estrader.android.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.easw.estrader.android.R

class StartingScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
}