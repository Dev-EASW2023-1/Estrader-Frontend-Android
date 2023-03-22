package kr.easw.estrader.android.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kr.easw.estrader.android.R
import kr.easw.estrader.android.fragment.LoginFragment
import kr.easw.estrader.android.fragment.RegisterFragment


class MainActivity : AppCompatActivity() {

    private val textView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

        val myFragment = LoginFragment()
        val myFragment2 = RegisterFragment()

        fragmentTransaction.add(R.id.login_container_view, myFragment)
        fragmentTransaction.commit()

        val signUpTextView = findViewById<TextView>(R.id.sign_up)
        signUpTextView.setOnClickListener {
            val newFragmentTransaction = supportFragmentManager.beginTransaction()
            newFragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            newFragmentTransaction.replace(R.id.login_container_view, myFragment2)
            newFragmentTransaction.commit()
        }

        val signInTextView = findViewById<TextView>(R.id.sign_in)
        signInTextView.setOnClickListener {
            val realNewFragmentTransaction = supportFragmentManager.beginTransaction()
            realNewFragmentTransaction.setCustomAnimations(
                R.anim.slide_out_left,
                R.anim.slide_in_right
            )
            realNewFragmentTransaction.replace(R.id.login_container_view, myFragment)
            realNewFragmentTransaction.commit()

            val imageButton = findViewById<Button>(R.id.btnNext)
            imageButton.setOnClickListener {
                val intent = Intent(this, MainListActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}






