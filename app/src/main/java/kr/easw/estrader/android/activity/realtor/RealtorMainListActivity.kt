package kr.easw.estrader.android.activity.realtor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kr.easw.estrader.android.databinding.ActivityRealtorMainlistBinding
import kr.easw.estrader.android.fragment.realtor.DelegateCompletedFragment
import kr.easw.estrader.android.fragment.realtor.DelegateWaitingFragment

/**
 * 대리인 전용 메인화면 Activity
 * 상단 탭에서 대리위임 신청 리스트 (DelegateFragment), 대리위임 완료 리스트 (DelegateCompletionFragment) 이동
 *
 * 사용 미정
 */
class RealtorMainListActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityRealtorMainlistBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        const val NUM_TABS = 2
        val tabTitleArray = arrayOf(
            "대리 위임 진행 목록",
            "대리 위임 완료 목록",
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityRealtorMainlistBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        initFields()
        initTabLayout()
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
                        0 -> DelegateWaitingFragment()
                        else -> DelegateCompletedFragment()
                    }
                }
            }
        // TabLayout 각 tab 의 구성 값 설정
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }
}