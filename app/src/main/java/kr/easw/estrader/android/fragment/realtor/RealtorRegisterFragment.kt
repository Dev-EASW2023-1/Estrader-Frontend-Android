package kr.easw.estrader.android.fragment.realtor

import android.app.Dialog
import android.content.Intent
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
import kr.easw.estrader.android.activity.realtor.RealtorAwaitingActivity
import kr.easw.estrader.android.databinding.FragmentRealtorRegisterBinding
import kr.easw.estrader.android.definitions.*
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.RealtorRegisterDataRequest
import kr.easw.estrader.android.model.dto.RealtorSignupCheckRequest
import kr.easw.estrader.android.util.PreferenceUtil

/**
 * 대리인 전용 회원가입 Fragment
 * 회원가입 완료 후 RealtorDialog 로 이동
 */
class RealtorRegisterFragment : Fragment() {
    private var _binding: FragmentRealtorRegisterBinding? = null
    private val binding get() = _binding!!
    private var validate = false
    private var validateRealtorId: String = ""
    private val registerButton: Button by lazy {
        binding.btnNext
    }
    private val checkButton: Button by lazy {
        binding.isIdDuplicated
    }
    private val realtorId: TextInputLayout by lazy {
        binding.userId
    }
    private val realtorPw: TextInputLayout by lazy {
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
        _binding = FragmentRealtorRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initFields()

        checkButton.setOnClickListener {
            val inputId = realtorId.editText!!.text.toString()
            if (inputId.isEmpty()) {
                return@setOnClickListener
            }

            validateRealtorId(inputId)
        }

        registerButton.setOnClickListener {
            val inputId = realtorId.editText!!.text.toString()
            val inputPw = realtorPw.editText!!.text.toString()
            val inputCheckPassword = userPwRepeat.editText!!.text.toString()

            if (!validate) {
                showToast("아이디 중복 확인해주세요.")
                return@setOnClickListener
            }
            if (validateRealtorId != inputId){
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
            if (PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_FCM) == "") {
                showToast("회원가입에 실패했어요. 다시 한 번 시도해주세요.")
                return@setOnClickListener
            }

            registerRealtor(inputId, inputPw)
        }
    }

    private fun initFields() {
        registerButton
        checkButton
        realtorId
        realtorPw
        userPwRepeat
    }

    private fun validateRealtorId(realtorId: String) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.REALTOR_CHECK_ID_DUPLICATED
            .setRequestParams(RealtorSignupCheckRequest(realtorId))
            .setListener {
                showToast(it.message)
                validate = it.isDuplicated
                validateRealtorId = realtorId

                dialog.dismiss()
            }
            .build(requireContext())
    }

    private fun registerRealtor(realtorId: String, realtorPw: String) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.REALTOR_REGISTER_PROCESS
            .setRequestParams(
                RealtorRegisterDataRequest(
                    realtorId,
                    realtorPw,
                    "test",
                    "test",
                    "test",
                    "test",
                    "test",
                    PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_FCM)!!,
                    "test"
                )
            )
            .setListener {
                showToast(it.message)
                if (it.isSuccess) {
                    PreferenceUtil(requireContext()).init().start()
                        .setString(PREFERENCE_REALTOR_ID, realtorId)
                        .setString(PREFERENCE_REALTOR_PW, realtorPw)
                        .setString(PREFERENCE_REALTOR_TOKEN, it.token)

                    println(PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_ID)!!)
                    println(PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_PW)!!)

                    requireActivity().startActivity<RealtorAwaitingActivity> {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
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

