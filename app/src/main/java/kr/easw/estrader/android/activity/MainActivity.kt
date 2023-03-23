package kr.easw.estrader.android.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commitNow
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityMainlistBinding
import kr.easw.estrader.android.fragment.LoginFragment
import kr.easw.estrader.android.fragment.MainListFragment
import kr.easw.estrader.android.fragment.RegisterFragment
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity() {

    /**
     * 프래그먼트 매니저, 트랜잭션 생성후 프래그먼트 생성 후 프레임에 표시 후
     * 각 텍스트퓨 클릭 이벤트
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val LoginFragment = LoginFragment()
        val RegisterFragment = RegisterFragment()

        fragmentTransaction.add(R.id.login_container_view, LoginFragment)
        fragmentTransaction.commit()

        val signUpTextView = findViewById<TextView>(R.id.sign_up)
        signUpTextView.setOnClickListener {
            signupclick(RegisterFragment)
        }
        val signInTextView = findViewById<TextView>(R.id.sign_in)
        signInTextView.setOnClickListener {
            signinclick(LoginFragment)
        }
    }

    //회원가입 텍스트뷰 클릭 이벤트
    private fun signupclick(RegisterFragment: RegisterFragment ) {
        val newFragmentTransaction = supportFragmentManager.beginTransaction()
        newFragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        newFragmentTransaction.replace(R.id.login_container_view, RegisterFragment)
        newFragmentTransaction.commit()
    }

    //로그인 텍스트뷰 클릭 이벤트
    private fun signinclick(LoginFragment: androidx.fragment.app.Fragment) {
        val realNewFragmentTransaction = supportFragmentManager.beginTransaction()
        realNewFragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
        realNewFragmentTransaction.replace(R.id.login_container_view, LoginFragment)
        realNewFragmentTransaction.commit()
    }
}








