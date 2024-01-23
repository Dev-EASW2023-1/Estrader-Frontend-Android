package kr.easw.estrader.android.fragment.user

import android.app.Dialog
import android.content.Context
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.user.MainListActivity
import kr.easw.estrader.android.activity.user.NavigationActivity
import kr.easw.estrader.android.databinding.FragmentLoginBinding
import kr.easw.estrader.android.databinding.FragmentLoginRev1Binding
import kr.easw.estrader.android.definitions.*
import kr.easw.estrader.android.extensions.replaceFragment
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.SignInRequest
import kr.easw.estrader.android.util.PreferenceUtil

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginRev1Binding? = null
    private val binding get() = _binding!!
    private val userId: TextInputLayout by lazy {
        binding.userId
    }
    private val userPw: TextInputLayout by lazy {
        binding.userPw
    }
    private val userPwNextButton: Button by lazy {
        binding.btnNext2
    }

    private val signUpButton: Button by lazy {
        binding.signIn
    }
    // Animation 객체 메모리 로딩
    private val fadeInAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
    }
    private val fadeOutAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_left)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 키보드 화면 덮는 현상 방지
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginRev1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFields()
        initButtonClickListener()
    }

    private fun initFields() {
        signUpButton
        userId
        userPw
        userPwNextButton
        fadeInAnimation
        fadeOutAnimation
    }

    private fun initButtonClickListener() {
        signUpButton.setOnClickListener {
            navigateToSignUpFragment()
        }


        userPwNextButton.setOnClickListener {
            loginUser()
        }
    }

    private fun navigateToSignUpFragment() {
        requireActivity().supportFragmentManager.replaceFragment<SignUpFragment>(
            R.id.container_view,
            null
        )
    }



    private fun loginUser() {
        val inputId = binding.userId.editText?.text.toString()
        val inputPw = binding.userPw.editText?.text.toString()

        if (PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM).isNullOrEmpty()) {
            showToast("로그인에 실패했습니다. 다시 한 번 시도해주세요.")
            return
        }

        val dialog = Dialog(requireContext()).apply {
            setContentView(ProgressBar(requireContext()))
            show()
        }

        ApiDefinition.LOGIN_PROCESS.setRequestParams(
            SignInRequest(
                inputId,
                inputPw,
                PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM)!!
            )
        ).setListener {
            showToast(it.message)
            if (it.isSuccess) {
                PreferenceUtil(requireContext()).init().start()
                    .setString(PREFERENCE_ID, inputId)
                    .setString(PREFERENCE_PW, inputPw)
                    .setString(PREFERENCE_TOKEN, it.token)

                requireActivity().startActivity<NavigationActivity> {
                    flags = FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP
                }
                requireActivity().finish()
            }
            dialog.dismiss()
        }.build(requireContext())
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
