package kr.easw.estrader.android.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity

class LoginFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // nextbtn 클릭 이벤트 해당 프래그먼트에 써야됨
        val nextButton = view.findViewById<Button>(R.id.btnNext)
        nextButton.setOnClickListener {
            val intent = Intent(activity, MainListActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}