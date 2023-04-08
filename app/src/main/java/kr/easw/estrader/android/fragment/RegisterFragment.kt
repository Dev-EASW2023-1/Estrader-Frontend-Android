package kr.easw.estrader.android.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

import kr.easw.estrader.android.databinding.FragmentRegisterBinding

/**
 * 회원가입 Fragment
 *
 * 회원가입 미구현
 */
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var nextButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nextButton = binding.btnNext

        // Login 버튼 누르면 MainListActivity 로 이동
        nextButton.setOnClickListener {
            val id = binding.userId.editText?.text.toString()
            val pw = binding.userPw.editText?.text.toString()
            val sharedPreferences =
                requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("id", id)
            editor.putString("pw", pw)
            editor.apply()
            println(id)
            println(pw)
        }
    }


}

