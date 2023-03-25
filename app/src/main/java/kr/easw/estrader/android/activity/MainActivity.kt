package kr.easw.estrader.android.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kr.easw.estrader.android.R
import kr.easw.estrader.android.fragment.LoginFragment
import kr.easw.estrader.android.fragment.RegisterFragment

/**
 * 로그인, 회원 가입 activity
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
        initFragment()

        signInTextView.setOnClickListener {
            signInClick()
        }
        signUpTextView.setOnClickListener {
            signUpClick()
        }
    }

    //회원 가입, 로그인 Textview 초기화
    private fun initFields(){
        signInTextView
        signUpTextView
    }

    // 로그인 Fragment init
    private fun initFragment(){
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








