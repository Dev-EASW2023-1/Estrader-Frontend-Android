package kr.easw.estrader.android.fragment

import android.app.ProgressDialog
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
import com.android.volley.Request
import kr.easw.estrader.android.activity.MainListActivity
import kr.easw.estrader.android.databinding.FragmentLoginBinding
import kr.easw.estrader.android.model.dto.RegisterDataRequest
import kr.easw.estrader.android.model.dto.RegisterDataResponse
import kr.easw.estrader.android.util.RestRequestTemplate
import java.security.MessageDigest

/**
 * 로그인 Fragment
 * Login 버튼 누르면 MainListActivity 로 이동
 */
class LoginFragment : Fragment() {
    //    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val fragmentTag = "LoginFragmentLog"
    private val loginButton: Button by lazy {
        binding.btnNext
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(fragmentTag, "onCreate()")

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
        Log.d(fragmentTag, "onCreateView()")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(fragmentTag, "onViewCreated()")

        val userId = binding.userId.editText!!.text.toString()
        val userPw = binding.userPw.editText!!.text.toString()
        loginButton

        loginButton.setOnClickListener {
            startActivity(Intent(requireContext(), MainListActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            requireActivity().finish()
        }

//        loginButton.setOnClickListener {
//            if(userId!!.isEmpty() || userPw!!.isEmpty()){
//                return@setOnClickListener
//            }
//
//            val progressDialog = ProgressDialog(requireContext())
//            progressDialog.setMessage("Loading...")
//            progressDialog.show()
//
//            RestRequestTemplate.Builder<RegisterDataRequest, RegisterDataResponse>()
//                .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
//                .setRequestUrl("http://172.17.0.30:8060/user/register")
//                .setRequestParams(RegisterDataRequest(userId!!, userPw!!, "야옹", "야옹", "야옹"))
//                .setResponseParams(RegisterDataResponse::class.java)
//                .setRequestMethod(Request.Method.POST)
//                .setListener {
//                    if(it.isSuccess){
//                        startActivity(Intent(requireContext(), MainListActivity::class.java).apply {
//                            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                        })
//                        requireActivity().finish()
//                    } else {
//                        return@setListener
//                    }
//                    progressDialog.dismiss()
//                }
//                .build(requireContext())
//        }

//        nextButton = binding.btnNext
//        sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
//
//        userid = sharedPreferences.getString("id", "")
//        userpw = sharedPreferences.getString("hashedpw", "")
//
//
//        // Login 버튼 누르면 MainListActivity 로 이동
//        nextButton.setOnClickListener {
//            val repeat = AlertDialog.Builder(requireContext())
//
//            val inputid = binding.userId.editText!!.text.toString()
//            val inputpw = binding.userPw.editText!!.text.toString()
//            val hashedpw = sha256(inputpw)
//            if (inputid.equals(userid)&&hashedpw.equals(userpw)){
//                startActivity(Intent(requireContext(), MainListActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                })
//                requireActivity().finish()
//                println("입력비밀번호 : " + inputpw)
//                println("해시비밀번호 : " + hashedpw)
//            }
//            else{
//                repeat.setTitle("로그인 실패 ")
//                repeat.setMessage("아이디 혹은 비밀번호가 다릅니다.")
//                val dialog = repeat.create()
//                dialog.show()
//            }
//
//
//        }
    }

    // 생명 주기 테스트 용
    override fun onDestroyView() {
        Log.d(fragmentTag, "onDestroyView()")
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(fragmentTag, "onAttach()")
    }

    override fun onStart() {
        super.onStart()
        Log.d(fragmentTag, "onStart()")
    }

    override fun onResume() {
        super.onResume()
        Log.d(fragmentTag, "onResume()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(fragmentTag, "onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(fragmentTag, "onDestroy()")
    }

    fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}