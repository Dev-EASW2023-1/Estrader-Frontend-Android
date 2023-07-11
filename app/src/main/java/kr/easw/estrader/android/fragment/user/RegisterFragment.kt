package kr.easw.estrader.android.fragment.user

import android.app.Dialog
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kr.easw.estrader.android.activity.user.MainListActivity
import kr.easw.estrader.android.databinding.FragmentRegisterBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_FCM
import kr.easw.estrader.android.definitions.PREFERENCE_ID
import kr.easw.estrader.android.definitions.PREFERENCE_PW
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.RegisterDataRequest
import kr.easw.estrader.android.model.dto.SignupCheckRequest
import kr.easw.estrader.android.util.PreferenceUtil

/**
 * 회원가입 Fragment
 * 회원가입 완료 후 MainListActivity 로 이동
 */
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var validate = false
    private var validateUserId: String = ""
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

        // 키보드 화면 덮는 현상 방지
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
                showToast("아이디 중복 확인해주세요.")
                return@setOnClickListener
            }
            if(validateUserId != inputId){
                showToast("아이디 중복 다시 확인해주세요.")
                return@setOnClickListener
            }
            if (inputId.isEmpty() || inputPw.isEmpty() || inputCheckPassword.isEmpty()) {
                showToast("아이디 또는 비밀번호를 채워주세요.")
                return@setOnClickListener
            }
            if (inputPw != inputCheckPassword) {
                showToast("비밀번호와 비밀번호 확인이 일치하지 않아요.")
                return@setOnClickListener
            }
            if (PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM) == "") {
                showToast("회원가입에 실패했어요. 다시 한 번 시도해주세요.")
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
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.CHECK_ID_DUPLICATED
            .setRequestParams(SignupCheckRequest(userId))
            .setListener {
                showToast(it.message)
                validate = it.isDuplicated
                validateUserId = userId

                dialog.dismiss()
            }
            .build(requireContext())
    }

    private fun registerUser(userId: String, userPw: String) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.REGISTER_PROCESS
            .setRequestParams(
                RegisterDataRequest(
                    userId,
                    userPw,
                    "test",
                    "test",
                    "test",
                    "test",
                    "test",
                    PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM)!!,
                    "test"
                )
            )
            .setListener {
                showToast(it.message)
                if (it.isSuccess) {
                    PreferenceUtil(requireContext()).init().start()
                        .setString(PREFERENCE_ID, userId)
                        .setString(PREFERENCE_PW, userPw)

                    println(PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_ID)!!)
                    println(PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_PW)!!)

                    requireActivity().startActivity<MainListActivity> {
                        flags = FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP
                    }
                    requireActivity().finish()
                }

                dialog.dismiss()
            }
            .build(requireContext())
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

