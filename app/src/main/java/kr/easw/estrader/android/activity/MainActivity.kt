package kr.easw.estrader.android.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.fragment.LoginFragment
import kr.easw.estrader.android.fragment.RegisterFragment

/**
 * 공용 로그인, 회원가입 activity
 * 상단 탭에서 로그인 (LoginFragment), 회원가입 (RegisterFragment) 이동
 * LOGIN 버튼을 누르면 MainListActivity 로 이동
 */
class MainActivity : AppCompatActivity() {
    private val signInTextView: TextView by lazy {
        findViewById(R.id.sign_in)
    }
    private val signUpTextView: TextView by lazy {
        findViewById(R.id.sign_up)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initFields()

        //로그인 (LoginFragment) 초기화
        initFragment()

        //로그인 Textview 클릭 이벤트
        signInTextView.setOnClickListener {
            signInClick()
        }
        //회원 가입 Textview 클릭 이벤트
        signUpTextView.setOnClickListener {
            signUpClick()
        }
    }

    private fun initFields(){
        signInTextView
        signUpTextView
    }

    private fun initFragment(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container_view, LoginFragment())
            .commit()
    }

    private fun signInClick() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.container_view, LoginFragment())
            .commit()
    }

    private fun signUpClick() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            .replace(R.id.container_view, RegisterFragment())
            .commit()
    }
}