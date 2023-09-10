package kr.easw.estrader.android.fragment.user

import android.app.Dialog
import android.content.Context
import android.content.Intent
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
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.user.MainListActivity
import kr.easw.estrader.android.databinding.FragmentSignupBinding
import kr.easw.estrader.android.definitions.*
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.RegisterDataRequest
import kr.easw.estrader.android.model.dto.SignupCheckRequest
import kr.easw.estrader.android.util.PreferenceUtil

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private var validate = false
    private var validateUserId: String = ""
    private var currentStep = 1
    private val instructionTextView: TextView by lazy {
        binding.namePlz
    }
    private val isIdDuplicated: Button by lazy {
        binding.isIdDuplicated
    }
    private val userName: TextInputLayout by lazy {
        binding.userName
    }
    private val userPhoneNum: TextInputLayout by lazy {
        binding.userPhoneNum
    }
    private val userIdLayout: LinearLayout by lazy {
        binding.userIdLayout
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
    private val nextButton: Button by lazy {
        binding.nextButton
    }
    // Animation 객체 메모리 로딩
    private val slideInUpAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_sign_up)
    }
    private val shakeAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(requireContext(), R.anim.shake_animation)
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
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFields()
        initButtonClickListener()
    }

    private fun initFields() {
        instructionTextView
        isIdDuplicated
        userName
        userPhoneNum
        userIdLayout
        userId
        userPw
        userPwRepeat
        nextButton
        slideInUpAnimation
        shakeAnimation
    }

    private fun initButtonClickListener() {
        nextButton.setOnClickListener {
            val editName = binding.userName.editText?.text.toString()
            val editPhoneNum = binding.userPhoneNum.editText?.text.toString()
            val editId = userId.editText!!.text.toString()
            val editPw = userPw.editText!!.text.toString()
            val editCheckPassword = userPwRepeat.editText!!.text.toString()

            when (currentStep) {
                1 -> ifValidProceed(editName) {
                    setUserInfoFromEditText("휴대폰 번호를 입력해주세요.", userPhoneNum)
                    currentStep = 2
                }
                2 -> ifValidProceed(editPhoneNum) {
                    setUserInfoFromEditText("사용하실 아이디를 입력해주세요.", userIdLayout)
                    currentStep = 3
                }
                3 -> ifValidProceed(editId, validate){
                    setUserInfoFromEditText("사용하실 비밀번호를 입력해주세요.", userPw, userPwRepeat)
                    currentStep = 4
                }
                4 -> ifValidProceed(editPw, editCheckPassword) {
                    registerUser(editId, editPw, editName, editPhoneNum)
                }
            }
        }

        isIdDuplicated.setOnClickListener {
            val editId = userId.editText!!.text.toString()
            validateId(editId)
        }
    }

    private fun validateId(editId: String) {
        if (editId.isNotEmpty()) {
            validateUserId(editId)
        }
    }

    private fun validateUserId(userId: String) {
        val dialog = showDialogWithProgressBar(requireContext())
        ApiDefinition.CHECK_ID_DUPLICATED
            .setRequestParams(SignupCheckRequest(userId))
            .setListener {
                showToast(it.message)
                validate = it.isDuplicated
                validateUserId = userId

                // 중복된 아이디 일 경우
                if (!validate) {
                    binding.root.startAnimation(shakeAnimation)
                }

                dialog.dismiss()
            }.build(requireContext())
    }


    private fun registerUser(userId: String, userPw: String, userName: String, userPhoneNum: String) {
        val dialog = showDialogWithProgressBar(requireContext())
        ApiDefinition.REGISTER_PROCESS.setRequestParams(
            RegisterDataRequest(
                userId,
                userPw,
                userName,
                "test",
                userPhoneNum,
                "test",
                "test",
                PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM)!!,
                "test"
            )
        ).setListener {
            showToast(it.message)
            if (it.isSuccess) {
                PreferenceUtil(requireContext()).init().start()
                    .setString(PREFERENCE_ID, userId)
                    .setString(PREFERENCE_PW, userPw)
                    .setString(PREFERENCE_TOKEN, it.token)

                requireActivity().startActivity<MainListActivity> {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                requireActivity().finish()
            }

            dialog.dismiss()
        }.build(requireContext())
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showKeyboard(view: View) {
        if(Build.VERSION.SDK_INT>32){
            Log.d(":) | API_VERSION ", "showKeyboard: ${Build.VERSION.SDK_INT}")
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
        else{
            Log.d(":) | API_VERSION ", "showKeyboard: ${Build.VERSION.SDK_INT}")
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }

    private fun showDialogWithProgressBar(
        context: Context
    ): Dialog {
        return Dialog(context).apply {
            setContentView(ProgressBar(context))
            show()
        }
    }

    private fun setUserInfoFromEditText(
        instructionText: String,
        textInputLayout: TextInputLayout

    ){
        instructionTextView.text = instructionText
        textInputLayout.apply {
            visibility = View.VISIBLE
            startAnimation(slideInUpAnimation)
            requestFocus()
            showKeyboard(this)
            binding.userName.isEnabled = false;
        }
    }

    private fun setUserInfoFromEditText(
        instructionText: String,
        linearLayout: LinearLayout
    ){
        instructionTextView.text = instructionText
        linearLayout.apply {
            visibility = View.VISIBLE
            startAnimation(slideInUpAnimation)
            requestFocus()
            showKeyboard(this)
            binding.userPhoneNum.isEnabled = false;
        }
    }


    private fun setUserInfoFromEditText(
        instructionText: String,
        userPwInputLayout: TextInputLayout,
        userPwRepeatInputLayout: TextInputLayout
    ) {
        instructionTextView.text = instructionText
        userPwInputLayout.apply {
            visibility = View.VISIBLE
            startAnimation(slideInUpAnimation)
            requestFocus()
            showKeyboard(this)
            binding.userId.isEnabled = false;
        }
        userPwRepeatInputLayout.apply {
            visibility = View.VISIBLE
            startAnimation(slideInUpAnimation)
        }
    }

    private fun ifValidProceed(input: String, action: () -> Unit) {
        if (input.isEmpty()) {
            showErrorMessage()
            return
        }

        action.invoke()
    }

    private fun ifValidProceed(id: String, validate: Boolean, action: () -> Unit) {
        if(id != validateUserId) {
            showErrorMessage()
            return
        }

        if (!validate) {
            showErrorMessage()
            return
        }

        action.invoke()
    }

    private fun ifValidProceed(pw: String, pwRepeat: String, action: () -> Unit) {
        if (pw.isEmpty() || pwRepeat.isEmpty()) {
            showErrorMessage()
            return
        }

        if (pw != pwRepeat) {
            showErrorMessage()
            return
        }

        if (PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM) == "") {
            showToast("회원가입에 실패했어요. 다시 한 번 시도해주세요.")
            return
        }

        action.invoke()
    }

    private fun showErrorMessage() {
        when (currentStep) {
            1 -> showToast("이름을 채워주세요.")
            2 -> showToast("휴대폰 번호를 채워주세요.")
            3 -> showToast("아이디 점검을 다시 해주세요.")
            4 -> showToast("비밀번호 점검을 다시 해주세요.")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
