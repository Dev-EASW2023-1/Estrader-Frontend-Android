package kr.easw.estrader.android.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kr.easw.estrader.android.R
import kr.easw.estrader.android.fragment.LoginFragment
import kr.easw.estrader.android.fragment.RegisterFragment


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        //프래그먼트 매니저, 트랜잭션 생성후 프래그먼트 생성
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val myFragment = LoginFragment()
        val myFragment2 = RegisterFragment()
        //로그인 프래그먼트 화면에 추가
        fragmentTransaction.add(R.id.login_container_view, myFragment)
        fragmentTransaction.commit()
        //회원가입 텍스트뷰 클릭 이벤트
        val signUpTextView = findViewById<TextView>(R.id.sign_up)
        signUpTextView.setOnClickListener {
            val newFragmentTransaction = supportFragmentManager.beginTransaction()
            newFragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            newFragmentTransaction.replace(R.id.login_container_view, myFragment2)
            newFragmentTransaction.commit()
        }
        //로그인 텍스트뷰 클릭 이벤트
        val signInTextView = findViewById<TextView>(R.id.sign_in)
        signInTextView.setOnClickListener {
            //로그인 프래그먼트 화면에 추가
            val realNewFragmentTransaction = supportFragmentManager.beginTransaction()
            realNewFragmentTransaction.setCustomAnimations(
                R.anim.slide_in_right, R.anim.slide_out_left
            )
            realNewFragmentTransaction.replace(R.id.login_container_view, myFragment)
            realNewFragmentTransaction.commit()
        }
    }
}






