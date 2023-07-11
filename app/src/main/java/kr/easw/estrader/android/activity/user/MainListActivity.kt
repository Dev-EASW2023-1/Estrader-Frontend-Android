package kr.easw.estrader.android.activity.user

import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
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

    // 화면 상호 작용 시 자동 소프트 키보드 숨김
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(event)
    }
    override fun onStop() {
        super.onStop()
    }
}