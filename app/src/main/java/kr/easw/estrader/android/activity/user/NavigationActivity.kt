package kr.easw.estrader.android.activity.user

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityNavigationBinding
import kr.easw.estrader.android.extensions.replaceFragment
import kr.easw.estrader.android.fragment.user.HomeFragment
import kr.easw.estrader.android.fragment.user.ItemLookUpFragment
import kr.easw.estrader.android.fragment.user.MyprofileFragment
import kr.easw.estrader.android.fragment.user.SearchnewFragment
import kr.easw.estrader.android.fragment.user.SearchregionFragment

class NavigationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNavigationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigationDrawer()
        initTabLayout()
        initFragment()

        // 키보드 화면 덮는 현상 방지
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    private fun initNavigationDrawer() {
        binding.mainlistNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    binding.tabLayout.visibility = View.GONE

                    supportFragmentManager.replaceFragment<HomeFragment>(
                        binding.containerView.id, null
                    )
                    true
                }

                R.id.navigation_map -> {
                    binding.tabLayout.visibility = View.VISIBLE


                    supportFragmentManager.replaceFragment<SearchnewFragment>(
                        binding.containerView.id, null
                    )
                    true
                }

                R.id.navigation_more -> {
                    binding.tabLayout.visibility = View.VISIBLE


                    supportFragmentManager.replaceFragment<SearchregionFragment>(
                        binding.containerView.id, null
                    )
                    true
                }

                R.id.navigation_myinfo -> {
                    binding.tabLayout.visibility = View.GONE


                    supportFragmentManager.replaceFragment<MyprofileFragment>(
                        binding.containerView.id, null
                    )
                    true
                }

                else -> false
            }
        }
    }

    private fun initTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        supportFragmentManager.replaceFragment<SearchnewFragment>(
                            binding.containerView.id, null
                        )
                    }

                    1 -> {
                        supportFragmentManager.replaceFragment<SearchregionFragment>(
                            binding.containerView.id, null
                        )
                    }

                    2 -> {
                        supportFragmentManager.replaceFragment<ItemLookUpFragment>(
                            binding.containerView.id, null
                        )
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun initFragment() {
        supportFragmentManager.replaceFragment<SearchregionFragment>(
            binding.containerView.id, null
        )
    }


    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(event)
    }


}