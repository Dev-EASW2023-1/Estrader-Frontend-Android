package kr.easw.estrader.android.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kr.easw.estrader.android.databinding.FragmentRegisterBinding
import kr.easw.estrader.android.definitions.SERVER_URL
import java.security.MessageDigest

/**
 * 회원가입 Fragment
 *
 * 회원가입 미구현
 */
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var nextButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        nextButton = binding.btnNext

        // Login 버튼 누르면 MainListActivity 로 이동
        nextButton.setOnClickListener {
            val id = binding.userId.editText!!.text.toString()
            val pw = binding.userPw.editText!!.text.toString()
            val pwrp = binding.userPwRepeat.editText!!.text.toString()
            val repeat = AlertDialog.Builder(requireContext())
            repeat.setTitle("비밀번호 불일치")
            repeat.setMessage("비밀번호가 일치하지 않습니다.")

            val dialog = repeat.create()

            if (pw.equals(pwrp)) {
                val sharedPreferences =
                    requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)


                val editor = sharedPreferences.edit()
                editor.putString("id", id)
                editor.putString("pw", pw)
                val hashedpw = sha256(pw)
                editor.putString("hashedpw", hashedpw)
                editor.apply()
                val userid = sharedPreferences.getString("id", "")
                val userpassword = sharedPreferences.getString("hashedpw", "")
                println(userid)
                println(userpassword)
                saveUserInfo(id, hashedpw)

            } else{
                dialog.show()
            }

        }
    }
    fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
    fun saveUserInfo(userid: String, hashedPw: String) {
        val url = "$SERVER_URL/user/test"
        val request = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener<String> { response ->
                Log.d(TAG, "response: $response")
                println("통과댐")
            },
            Response.ErrorListener { error ->
                Log.e(TAG, "error: $error")
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["period"] = userid
                params["information"] = hashedPw
                return params
            }
        }

        val queue = Volley.newRequestQueue(context)
        queue.add(request)
    }
}

