package kr.easw.estrader.android.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityMainlistBinding
import kr.easw.estrader.android.fragment.MainListFragment

/**
 * 사용자 메인 화면 activity
 * 부동산 매각 정보(담당 법원, 사건 번호) 리스트
 */
class MainListActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityMainlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainlistBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        // commit() 으로 Fragment Transaction 비동기 처리
        supportFragmentManager.commit {
            replace(R.id.framelayout, MainListFragment())
        }
    }
}