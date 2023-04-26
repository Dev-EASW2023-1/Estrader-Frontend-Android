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
import kr.easw.estrader.android.databinding.FragmentRealtorLoginBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_FCM
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_ID
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_PW
import kr.easw.estrader.android.dialog.RealtorDialog
import kr.easw.estrader.android.model.dto.RealtorSignInRequest
import kr.easw.estrader.android.util.HashUtil
import kr.easw.estrader.android.util.PreferenceUtil

/**
 * 대리인 전용 로그인 Fragment
 * Login 버튼 누르면 RealtorDialog 로 이동
 */
class RealtorLoginFragment : Fragment() {
    private var _binding: FragmentRealtorLoginBinding? = null
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
        _binding = FragmentRealtorLoginBinding.inflate(inflater, container, false)
        Log.d(fragmentTag, "onCreateView()")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(fragmentTag, "onViewCreated()")

        initFields()

        loginButton.setOnClickListener {
            if (PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_FCM) == "") {
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

        ApiDefinition.REALTOR_LOGIN_PROCESS
            .setRequestParams(
                RealtorSignInRequest(
                    userId,
                    HashUtil.sha256(userPw),
                    PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_FCM)!!
                )
            )
            .setListener {
                showToast(it.message)
                if (it.isSuccess) {
                    PreferenceUtil(requireContext())
                        .init().start()
                        .setString(PREFERENCE_REALTOR_ID, userId)
                        .setString(PREFERENCE_REALTOR_PW, HashUtil.sha256(userPw))

                    startActivity(Intent(requireContext(), RealtorDialog::class.java).apply {
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