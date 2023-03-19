package kr.easw.estrader.android.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityMainBinding

class MainListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainlist)
        binding = ActivityMainBinding.inflate(layoutInflater)
    }
}