package kr.easw.estrader.android.activity

import android.os.Bundle
import android.text.TextUtils.replace
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityMainlistBinding
import kr.easw.estrader.android.fragment.MainListFragment

/**
 * 사용자 전용 메인화면 activity
 * 부동산 매각정보 리스트 (MainListFragment)
 */
class MainListActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityMainlistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainlistBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        // commit() 으로 Fragment Transaction 비동기 처리
        // 부동산 매각정보 리스트 (MainListFragment) 초기화
        supportFragmentManager.commit {
            replace(R.id.framelayout, MainListFragment())
        }
    }
    override fun onBackPressed() {
        supportFragmentManager.commit {
            replace(R.id.framelayout, MainListFragment())

        }}
}