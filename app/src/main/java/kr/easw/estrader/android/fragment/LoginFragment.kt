package kr.easw.estrader.android.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity

/**
 * 공용 로그인 Fragment
 * Login 버튼 누르면 MainListActivity 로 이동
 */
class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        val nextButton = view.findViewById<Button>(R.id.btnNext)

        // Login 버튼 누르면 MainListActivity 로 이동
        nextButton.setOnClickListener {
            startActivity(Intent(requireContext(), MainListActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            requireActivity().finish()
        }
    }
}