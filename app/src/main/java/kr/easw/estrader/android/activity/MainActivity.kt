package kr.easw.estrader.android.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ActivityLoginBinding
import kr.easw.estrader.android.fragment.LoginFragment
import kr.easw.estrader.android.fragment.RegisterFragment

/**
 * 로그인, 회원 가입 activity
 * LOGIN 버튼을 누르면 MainListActivity 로 이동
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFragment()

        binding.signIn.setOnClickListener {
            signInClick()
        }
        binding.signUp.setOnClickListener {
            signUpClick()
        }
    }


    // 로그인 Fragment init
    private fun initFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_view, LoginFragment())
            .commit()
    }

    //회원 가입 Textview 클릭 이벤트
    private fun signUpClick() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.container_view, RegisterFragment())
            .commit()
    }

    //로그인 Textview 클릭 이벤트
    private fun signInClick() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.container_view, LoginFragment())
            .commit()
    }
}