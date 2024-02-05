package kr.easw.estrader.android.fragment.user

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kr.easw.estrader.android.databinding.FragmentItemDetailBinding
import kr.easw.estrader.android.fragment.BaseFragment

class ItemDetailFragment : BaseFragment<FragmentItemDetailBinding>(FragmentItemDetailBinding::inflate) {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        const val NUM_TABS = 2
        const val ARG_POSITION = "position"
        val tabTitleArray = arrayOf(
            "기본",
            "사진"
        )
        fun indexImage(iconDrawable: String) = ItemDetailFragment().apply {
            arguments = bundleOf(ARG_POSITION to iconDrawable)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageViewWithGlide()
        initFields()
        initTabLayout()
    }

    private fun setImageViewWithGlide() {
        Glide.with(this)
            .load(arguments?.getString(ARG_POSITION).toString())
            .into(binding.mainimage)
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
                    0 -> ItemBasicInfoFragment()
                    else -> ItemPhotosFragment()
                }
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }
}