package kr.easw.estrader.android.fragment.delegation.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kr.easw.estrader.android.databinding.FragmentItemListBinding
import kr.easw.estrader.android.fragment.BaseFragment

class ItemListFragment : BaseFragment<FragmentItemListBinding>(FragmentItemListBinding::inflate) {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        const val NUM_TABS = 2
        val tabTitleArray = arrayOf(
            "오늘 신규 물건",
            "예정"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFields()
        initTabLayout()
    }

    private fun initFields() {
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
    }

    private fun initTabLayout() {
        viewPager.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return NUM_TABS
            }

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> NewItemListFragment()
                    else -> ScheduledItemListFragment()
                }
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }
}