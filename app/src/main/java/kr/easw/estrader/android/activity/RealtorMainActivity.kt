package kr.easw.estrader.android.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityRealtormainBinding
import kr.easw.estrader.android.fragment.DelegateCompletionFragment
import kr.easw.estrader.android.fragment.DelegateFragment

/**
 * 대리인 전용 메인화면 Activity
 * 상단 탭에서 대리위임 신청 리스트 (DelegateFragment), 대리위임 완료 리스트 (DelegateCompletionFragment) 이동
 *
 * 지금은 5초 뒤 "김덕배 님이 대리 위임을 신청하셨습니다." 팝업 출력
 * 추후 사용자 앱에서 FCM 전송 후, 팝업 출력
 */
class RealtorMainActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityRealtormainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var mHandler: Handler

    companion object {
        const val NUM_TABS = 2
        val tabTitleArray = arrayOf(
            "대리 위임 신청 목록",
            "대리 위임 완료 목록",
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityRealtormainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        initFields()
        initTabLayout()

        mHandler = Handler(Looper.getMainLooper())
        // 5초 뒤 "김덕배 님이 대리 위임을 신청하셨습니다." 팝업 확인 후, AwaitingBidDialog 이동
        mHandler.postDelayed({
            showDialog()
        }, 3000)
    }

    private fun initFields() {
        viewPager = activityBinding.viewPager
        tabLayout = activityBinding.tabLayout
    }


    private fun initTabLayout() {
        //ViewPager2 adapter, 범위 밖의 Fragment 객체는 FragmentManager 에서 제거, 제거된 Fragment 상태는 FragmentStatePagerAdapter 내부 저장
        viewPager.adapter =
            object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
                override fun getItemCount(): Int {
                    return NUM_TABS
                }

                override fun createFragment(position: Int): Fragment {
                    return when (position) {
                        0 -> DelegateFragment()
                        else -> DelegateCompletionFragment()
                    }
                }
            }
        // TabLayout 각 tab 의 구성 값 설정
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }

    private fun showDialog() {
        AlertDialog.Builder(this, R.style.AppTheme_AlertDialogTheme)
            .setTitle("알림")
            .setMessage("김덕배 님이 대리 위임을 신청하셨습니다.")
            .setPositiveButton("확인") { _, _ ->
                // TODO("대리 위임 완료 리스트 추가")
            }
            .setNegativeButton("취소") { _, _ ->
            }
            .create()
            .show()
    }

    // Runnable, Handler 객체 모두 메모리 누수 유발 방지
    override fun onDestroy() {
        super.onDestroy()
        // Handler 모든 콜백 및 메시지 제거
        mHandler.removeCallbacksAndMessages(null)
        //  이전 Handler 객체와 연결을 끊고 새로운 Looper 로 새 Handler 객체 초기화
        mHandler = Handler(Looper.getMainLooper())
    }
}