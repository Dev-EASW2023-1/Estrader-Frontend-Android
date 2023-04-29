package kr.easw.estrader.android.activity.user

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import kr.easw.estrader.android.databinding.ActivityMainlistBinding
import kr.easw.estrader.android.fragment.user.MainListFragment

/**
 * 사용자 전용 메인화면 activity
 * 부동산 매각정보 리스트 (MainListFragment)
 */
class MainListActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityMainlistBinding
    private lateinit var callback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainlistBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                supportFragmentManager.commit {
                    replace(activityBinding.framelayout.id, MainListFragment())
                }
            }
        }
        this.onBackPressedDispatcher.addCallback(this, callback)

        // commit() 으로 Fragment Transaction 비동기 처리
        supportFragmentManager.commit {
            replace(activityBinding.framelayout.id, MainListFragment())
        }
    }
}