package kr.easw.estrader.android.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commitNow
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityRealtormainBinding
import kr.easw.estrader.android.fragment.DelegateCompletionFragment
import kr.easw.estrader.android.fragment.DelegateFragment

private const val NUM_TABS = 2

class RealtorMainActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityRealtormainBinding
    private val tabTitleArray = arrayOf(
        "대리 위임 신청 목록",
        "대리 위임 완료 목록",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityRealtormainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        val viewPager = activityBinding.viewPager
        val tabLayout = activityBinding.tabLayout

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }
}


class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

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