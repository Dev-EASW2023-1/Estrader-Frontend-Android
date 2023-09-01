package kr.easw.estrader.android.fragment.user

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.user.MainListActivity
import kr.easw.estrader.android.databinding.FragmentSignupBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_FCM
import kr.easw.estrader.android.definitions.PREFERENCE_ID
import kr.easw.estrader.android.definitions.PREFERENCE_PW
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.RegisterDataRequest
import kr.easw.estrader.android.model.dto.SignupCheckRequest
import kr.easw.estrader.android.util.PreferenceUtil

class SignupFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private var validate = false
    private var validateUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var currentStep = 1
        binding.nextButton.setOnClickListener {
            when (currentStep) {
                1 -> ifValidProceed(binding.username.editText?.text) { inputphonenum(); currentStep = 2 }
                2 -> ifValidProceed(binding.userphonenum.editText?.text) { inputid(); currentStep = 3 }
                3 -> handleIdValidationStep()
            }
        }
        binding.signIn.setOnClickListener { handleSignIn() }
    }

    private fun ifValidProceed(input: CharSequence?, action: () -> Unit) {
        if (!input.isNullOrBlank()) action.invoke()
    }

    private fun handleIdValidationStep() {
        val inputId = binding.userId.editText?.text.toString()
        if (inputId.isNotEmpty()) {
            validateUserId(inputId)
        }
    }


    private fun handleSignIn() {
        val inputId = binding.userId.editText?.text.toString()
        val inputPw = binding.userPw.editText?.text.toString()
        val inputCheckPassword = binding.userPwRepeat.editText?.text.toString()

        when {
            inputId.isEmpty() || inputPw.isEmpty() || inputCheckPassword.isEmpty() -> showToast("아이디 또는 비밀번호를 채워주세요.")
            inputPw != inputCheckPassword -> showToast("비밀번호와 비밀번호 확인이 일치하지 않아요.")
            PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM).isNullOrEmpty() -> showToast("회원가입에 실패했어요. 다시 한 번 시도해주세요.")
            !validate -> {
                showToast("아이디 중복을 확인해주세요.")
                inputphonenum()
            }
            else -> registerUser(inputId, inputPw)
        }
    }


    private fun showKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun showDialogWithProgressBar(): Dialog {
        return Dialog(requireContext()).apply {
            setContentView(ProgressBar(requireContext()))
            show()
        }
    }

    private fun inputphonenum() {
        showDialogWithProgressBar().apply {
            binding.namePlz.text = "폰번호를 입력해주세요"
            binding.userphonenum.apply {
                visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_signup))
            }
            binding.userphonenum.requestFocus()
            showKeyboard(binding.userphonenum)
            dismiss()
        }
    }

    private fun inputid() {
        showDialogWithProgressBar().apply {
            binding.namePlz.text = "사용하실 아이디를 입력해주세요"
            binding.userId.apply {
                visibility = View.VISIBLE
                startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_signup))
            }
            binding.userId.requestFocus()

            showKeyboard(binding.userId)
            dismiss()
        }
    }

    private fun inputpw() {
        showDialogWithProgressBar().apply {
            binding.apply {
                namePlz.text = "사용하실 비밀번호를 입력해주세요"
                nextButton.visibility = View.GONE
                signIn.visibility = View.VISIBLE
                userPw.visibility = View.VISIBLE
                userPwRepeat.visibility = View.VISIBLE

                val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_signup)
                userPw.startAnimation(slideUp)
                userPwRepeat.startAnimation(slideUp)
                signIn.startAnimation(slideUp)

                showKeyboard(userPw)
            }
            binding.userPw.requestFocus()

            dismiss()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun validateUserId(userId: String) {
        val dialog = showDialogWithProgressBar()
        val shake = AnimationUtils.loadAnimation(context, R.anim.shake_animation)
        ApiDefinition.CHECK_ID_DUPLICATED.setRequestParams(SignupCheckRequest(userId))
            .setListener {
                dialog.dismiss()

                validate = !it.isDuplicated
                validateUserId = userId

                // 중복된 아이디가 있을 경우
                if (validate) {
                    binding.root.startAnimation(shake)
                    showToast(it.message)
                } else {
                    // 중복된 아이디가 없을 경우
                    inputpw()
                }
            }.build(requireContext())
    }



    private fun registerUser(userId: String, userPw: String) {
        val dialog = showDialogWithProgressBar()
        ApiDefinition.REGISTER_PROCESS.setRequestParams(
            RegisterDataRequest(
                userId, userPw, "test", "test", "test", "test", "test",
                PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM)!!, "test"
            )
        ).setListener {
            showToast(it.message)
            if (it.isSuccess) {
                PreferenceUtil(requireContext()).init().start()
                    .setString(PREFERENCE_ID, userId)
                    .setString(PREFERENCE_PW, userPw)

                requireActivity().startActivity<MainListActivity> {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                requireActivity().finish()
            }
            dialog.dismiss()
        }.build(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
