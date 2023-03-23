package kr.easw.estrader.android.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kr.easw.estrader.android.databinding.ActivityRealtormainBinding
import kr.easw.estrader.android.dialog.AwaitingbidDialog
import kr.easw.estrader.android.fragment.DelegateCompletionFragment
import kr.easw.estrader.android.fragment.DelegateFragment

private const val NUM_TABS = 2

class RealtorMainActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityRealtormainBinding
    private lateinit var mHandler: Handler
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
        mHandler = Handler()

        // 5초 후에 팝업을 띄우기 위해 postDelayed 메소드를 호출
        mHandler.postDelayed({
            val dialog = AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("김덕배 님이 대리 위임을 신청하셨습니다.")
                .setPositiveButton("확인") { _, _ ->
                    accept()               // 확인 버튼 클릭 시 동작
                }
                .setNegativeButton("취소") { _, _ ->
                    // 취소 버튼 클릭 시 동작
                }
                .create()
            dialog.show()
        }, 3000) // 5초 (5000ms) 후에 팝업을 띄움
    }

    private fun accept() {
        // MainListActivity로 이동
        val intent = Intent(applicationContext, AwaitingbidDialog::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 핸들러의 메시지 큐에서 보류중인 메시지들을 모두 제거해줍니다.
        mHandler.removeCallbacksAndMessages(null)
    }


}

private class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
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