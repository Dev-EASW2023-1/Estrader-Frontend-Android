package kr.easw.estrader.android.dialog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.easw.estrader.android.R

class ErrorDialog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)
    }
}