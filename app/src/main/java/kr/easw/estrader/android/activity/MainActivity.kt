package kr.easw.estrader.android.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kr.easw.estrader.android.fragment.LoginFragment


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(kr.easw.estrader.android.R.layout.activity_login)
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val myFragment = LoginFragment()
        fragmentTransaction.add(kr.easw.estrader.android.R.id.login_container_view, myFragment)
        fragmentTransaction.commit()
        }
    }
