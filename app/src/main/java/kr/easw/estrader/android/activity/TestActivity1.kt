package kr.easw.estrader.android.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.airbnb.lottie.LottieAnimationView
import kr.easw.estrader.android.R
import kr.easw.estrader.android.fragment.AgentdelegationFragment
import kr.easw.estrader.android.fragment.LoginFragment
import kr.easw.estrader.android.fragment.RegisterFragment

class TestActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mainlist)

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val myFragment = AgentdelegationFragment()
        val myFragment2 = RegisterFragment()
        fragmentTransaction.add(kr.easw.estrader.android.R.id.mainlist_framelayout, myFragment)
        fragmentTransaction.commit()

    }
}