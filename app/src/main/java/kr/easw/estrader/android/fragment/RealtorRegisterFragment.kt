package kr.easw.estrader.android.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kr.easw.estrader.android.databinding.FragmentRealtorRegisterBinding
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_FCM
import kr.easw.estrader.android.util.PreferenceUtil

/**
 * 대리인 전용 회원가입 Fragment
 * 회원가입 완료 후 RealtorDialog 로 이동
 */
class RealtorRegisterFragment : Fragment() {
    private var _binding: FragmentRealtorRegisterBinding? = null
    private val binding get() = _binding!!
    private var validate = false
    private var validateUserId: String = ""
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
        _binding = FragmentRealtorRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initFields()

        checkButton.setOnClickListener {
            val inputId = userId.editText!!.text.toString()
            if (inputId.isEmpty()) {
                return@setOnClickListener
            }
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
            if (PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_FCM) == "") {
                showToast("회원가입에 실패했어요. 다시 한 번 시도해주세요.")
                return@setOnClickListener
            }
        }
    }

    private fun initFields() {
        registerButton
        checkButton
        userId
        userPw
        userPwRepeat
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

