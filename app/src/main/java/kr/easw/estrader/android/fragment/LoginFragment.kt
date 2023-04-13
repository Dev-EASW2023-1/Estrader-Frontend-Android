package kr.easw.estrader.android.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.activity.MainListActivity
import kr.easw.estrader.android.databinding.FragmentLoginBinding
import java.security.MessageDigest

/**
 * 로그인 Fragment
 * Login 버튼 누르면 MainListActivity 로 이동
 */
class LoginFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var nextButton: Button
    private var userid: String? = null
    private var userpw: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nextButton = binding.btnNext
        sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)

        userid = sharedPreferences.getString("id", "")
        userpw = sharedPreferences.getString("hashedpw", "")


        // Login 버튼 누르면 MainListActivity 로 이동
        nextButton.setOnClickListener {
            val repeat = AlertDialog.Builder(requireContext())

            val inputid = binding.userId.editText!!.text.toString()
            val inputpw = binding.userPw.editText!!.text.toString()
            val hashedpw = sha256(inputpw)
            if (inputid.equals(userid)&&hashedpw.equals(userpw)){
                startActivity(Intent(requireContext(), MainListActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                })
                requireActivity().finish()
                println("입력비밀번호 : " + inputpw)
                println("해시비밀번호 : " + hashedpw)
            }
            else{
                repeat.setTitle("로그인 실패 ")
                repeat.setMessage("아이디 혹은 비밀번호가 다릅니다.")
                val dialog = repeat.create()
                dialog.show()
            }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}