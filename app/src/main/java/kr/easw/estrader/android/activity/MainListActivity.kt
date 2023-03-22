package kr.easw.estrader.android.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commitNow
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityMainlistBinding
import kr.easw.estrader.android.fragment.MainListFragment

class MainListActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityMainlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainlistBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        supportFragmentManager.commitNow {
            replace(R.id.mainlist_framelayout, MainListFragment())
        }
    }

}