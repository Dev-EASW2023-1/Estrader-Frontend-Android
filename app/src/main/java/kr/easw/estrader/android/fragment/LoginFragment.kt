package kr.easw.estrader.android.fragment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kr.easw.estrader.android.activity.MainListActivity
import kr.easw.estrader.android.databinding.FragmentLoginBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_FCM
import kr.easw.estrader.android.definitions.PREFERENCE_ID
import kr.easw.estrader.android.definitions.PREFERENCE_PW
import kr.easw.estrader.android.model.dto.SignInRequest
import kr.easw.estrader.android.util.HashUtil
import kr.easw.estrader.android.util.PreferenceUtil

/**
 * 로그인 Fragment
 * Login 버튼 누르면 MainListActivity 로 이동
 */
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginButton: Button by lazy {
        binding.btnNext
    }
    private val userId: TextInputLayout by lazy {
        binding.userId
    }
    private val userPw: TextInputLayout by lazy {
        binding.userPw
    }

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
        initFields()

        loginButton.setOnClickListener {
            if (PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM) == "") {
                showToast("로그인에 실패했습니다. 다시 한 번 시도해주세요.")
                return@setOnClickListener
            }

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
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.LOGIN_PROCESS
            .setRequestParams(
                SignInRequest(
                    userId,
                    HashUtil.sha256(userPw),
                    PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM)!!
                )
            )
            .setListener {
                showToast(it.message)
                if (it.isSuccess) {
                    PreferenceUtil(requireContext())
                        .init().start()
                        .setString(PREFERENCE_ID, userId)
                        .setString(PREFERENCE_PW, HashUtil.sha256(userPw))

                    startActivity(Intent(requireContext(), MainListActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    })
                    requireActivity().finish()
                }

                dialog.dismiss()
            }
            .build(requireContext())
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}