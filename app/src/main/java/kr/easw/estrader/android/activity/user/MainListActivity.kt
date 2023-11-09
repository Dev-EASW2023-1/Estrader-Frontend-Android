package kr.easw.estrader.android.activity.user

import android.app.Dialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityMainlistBinding
import kr.easw.estrader.android.fragment.user.MainListFragment
import kr.easw.estrader.android.fragment.user.MapFragment


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

            //  R.id.navigation_home true 추가,R.id.navigation_home일때는 안되게 조건걹기
            override fun handleOnBackPressed() {

                AlertDialog.Builder(this@MainListActivity)
                    .setTitle("종료")
                    .setMessage("종료하시겠습니까?")
                    .setPositiveButton("확인") { _, _ ->
                        val dialog = Dialog(this@MainListActivity)
                        dialog.dismiss()
                        finish()
                    }
                    .setNegativeButton("취소") { _, _ ->
                    }
                    .create()
                    .show()

            }


        }
        this.onBackPressedDispatcher.addCallback(this, callback)
        // 현재 호출된 버튼일때 또 누르면 호출 안되게 변경 -> 안하면 계속 쌓임
        var currentSelectedItemId: Int? = null

        activityBinding.mainlistNavigationView.setOnItemSelectedListener { item ->
            if (currentSelectedItemId == item.itemId) return@setOnItemSelectedListener false

            println("item :) | ${item.itemId}")
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.commit {
                        replace(activityBinding.framelayout.id, MainListFragment())
                    }
                    currentSelectedItemId = item.itemId
                    true
                }

                R.id.navigation_map -> {
                    supportFragmentManager.commit {
                        replace(activityBinding.framelayout.id, MapFragment())
                    }
                    currentSelectedItemId = item.itemId
                    true
                }

                else -> false
            }
        }


        // commit() 으로 Fragment Transaction 비동기 처리
        supportFragmentManager.commit {
            replace(activityBinding.framelayout.id, MapFragment())
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