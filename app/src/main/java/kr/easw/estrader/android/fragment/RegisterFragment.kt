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
import kr.easw.estrader.android.databinding.FragmentRegisterBinding
import kr.easw.estrader.android.definitions.PREFERENCE_ID
import kr.easw.estrader.android.definitions.PREFERENCE_PW
import kr.easw.estrader.android.model.dto.RegisterDataRequest
import kr.easw.estrader.android.model.dto.RegisterDataResponse
import kr.easw.estrader.android.model.dto.SignupCheckRequest
import kr.easw.estrader.android.model.dto.SignupCheckResponse
import kr.easw.estrader.android.util.HashUtil
import kr.easw.estrader.android.util.PreferenceUtil
import kr.easw.estrader.android.util.RestRequestTemplate

/**
 * 회원가입 Fragment
 * 회원가입 완료 후 MainListActivity 로 이동
 */
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var validate = false
    private val fragmentTag = "RegisterFragmentLog"
    private val registerButton: Button by lazy {
        binding.btnNext
    }
    private val checkButton: Button by lazy {
        binding.isIdDuplicated
    }
    private val userId: TextInputLayout by lazy {
        binding.userId
    }
    private val userPw: TextInputLayout by lazy {
        binding.userPw
    }
    private val userPwRepeat: TextInputLayout by lazy {
        binding.userPwRepeat
    }

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
        initFields()

        checkButton.setOnClickListener {
            val inputId = userId.editText!!.text.toString()
            if (inputId.isEmpty()) {
                return@setOnClickListener
            }

            validateUserId(inputId)
        }

        registerButton.setOnClickListener {
            val inputId = userId.editText!!.text.toString()
            val inputPw = userPw.editText!!.text.toString()
            val inputCheckPassword = userPwRepeat.editText!!.text.toString()

            if (!validate) {
                return@setOnClickListener
            }
            if (inputId.isEmpty() || inputPw.isEmpty())
            {
                return@setOnClickListener
            }
            if (inputPw != inputCheckPassword) {
                return@setOnClickListener
            }

            registerUser(inputId, inputPw)
        }
    }

    private fun initFields() {
        registerButton
        checkButton
        userId
        userPw
        userPwRepeat
    }

    private fun validateUserId(userId: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        RestRequestTemplate.Builder<SignupCheckRequest, SignupCheckResponse>()
            .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
            .setRequestUrl("http://172.17.0.30:8060/user/account-exists")
            .setRequestParams(SignupCheckRequest(userId))
            .setResponseParams(SignupCheckResponse::class.java)
            .setRequestMethod(Request.Method.POST)
            .setListener {

                validate = it.isDuplicated
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                progressDialog.dismiss()
            }
            .build(requireContext())
    }

    private fun registerUser(userId: String, userPw: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        RestRequestTemplate.Builder<RegisterDataRequest, RegisterDataResponse>()
            .setRequestHeaders(mutableMapOf("Content-Type" to "application/json"))
            .setRequestUrl("http://172.17.0.30:8060/user/register")
            .setRequestParams(RegisterDataRequest(userId, HashUtil.sha256(userPw), "테스트", "테스트", "테스트"))
            .setResponseParams(RegisterDataResponse::class.java)
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

