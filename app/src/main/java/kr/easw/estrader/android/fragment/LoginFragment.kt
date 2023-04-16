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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.google.android.material.textfield.TextInputLayout
import kr.easw.estrader.android.activity.MainListActivity
import kr.easw.estrader.android.databinding.FragmentLoginBinding
import kr.easw.estrader.android.definitions.PREFERENCE_ID
import kr.easw.estrader.android.definitions.PREFERENCE_PW
import kr.easw.estrader.android.model.dto.SignInRequest
import kr.easw.estrader.android.model.dto.SignInResponse
import kr.easw.estrader.android.util.HashUtil
import kr.easw.estrader.android.util.PreferenceUtil
import kr.easw.estrader.android.util.RestRequestTemplate

/**
 * 로그인 Fragment
 * Login 버튼 누르면 MainListActivity 로 이동
 */
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val fragmentTag = "LoginFragmentLog"
    private val loginButton: Button by lazy {
        binding.btnNext
    }
    private val userId: TextInputLayout by lazy {
        binding.userId
    }
    private val userPw: TextInputLayout by lazy {
        binding.userPw
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

        initFields()

        loginButton.setOnClickListener {
            val inputId = userId.editText!!.text.toString()
            val inputPw = userPw.editText!!.text.toString()

            loginUser(inputId, inputPw)
        }
    }

    private fun initFields() {
        loginButton
        userId
        userPw
    }

    private fun loginUser(userId: String, userPw: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        RestRequestTemplate.Builder<SignInRequest, SignInResponse>()
            .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
            .setRequestUrl("http://172.17.0.30:8060/user/login")
            .setRequestParams(SignInRequest(userId, HashUtil.sha256(userPw)))
            .setResponseParams(SignInResponse::class.java)
            .setRequestMethod(Request.Method.POST)
            .setListener {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                if (it.isSuccess) {
                    PreferenceUtil(requireContext())
                        .init().build()
                        .setString(PREFERENCE_ID, userId)
                        .setString(PREFERENCE_PW, HashUtil.sha256(userPw))

                    startActivity(Intent(requireContext(), MainListActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    requireActivity().finish()
                }

                progressDialog.dismiss()
            }
            .build(requireContext())
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
}