package kr.easw.estrader.android.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import kr.easw.estrader.android.R
import kr.easw.estrader.android.fragment.AgentdelegationFragment
import kr.easw.estrader.android.fragment.LoginFragment
import kr.easw.estrader.android.fragment.RegisterFragment


class MainActivity : AppCompatActivity() {
    private val textView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(kr.easw.estrader.android.R.layout.activity_login)
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        val myFragment = LoginFragment()
        val myFragment2 = RegisterFragment()
        fragmentTransaction.add(kr.easw.estrader.android.R.id.login_container_view, myFragment)
        fragmentTransaction.commit()



        var tv = findViewById(R.id.sign_up) as TextView
        tv.setOnClickListener {
            val newFragmentTransaction = supportFragmentManager.beginTransaction() // 트랜잭션 재생성
            newFragmentTransaction.setCustomAnimations(R.anim.slide_in_right,  R.anim.slide_out_left)
            newFragmentTransaction.replace(R.id.login_container_view, myFragment2)
            newFragmentTransaction.addToBackStack(null)

            newFragmentTransaction.commit()
        }
        var tv2 = findViewById(R.id.sign_in) as TextView
        tv2.setOnClickListener {
            val realnewFragmentTransaction = supportFragmentManager.beginTransaction()
            realnewFragmentTransaction.setCustomAnimations(R.anim.slide_out_left,  R.anim.slide_in_right)
            realnewFragmentTransaction.replace(R.id.login_container_view, myFragment)
            realnewFragmentTransaction.addToBackStack(null)


            realnewFragmentTransaction.commit()
            val imageButton = findViewById<Button>(R.id.btnNext)
            imageButton.setOnClickListener {
                val intent = Intent(applicationContext, TestActivity2::class.java)
                startActivity(intent)
            }

        }




    }
        }






