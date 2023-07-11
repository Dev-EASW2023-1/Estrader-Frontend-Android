package kr.easw.estrader.android.fragment.realtor

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
import kr.easw.estrader.android.activity.realtor.RealtorAwaitingActivity
import kr.easw.estrader.android.databinding.FragmentRealtorLoginBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_FCM
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_ID
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_PW
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.RealtorSignInRequest
import kr.easw.estrader.android.util.PreferenceUtil

/**
 * 대리인 전용 로그인 Fragment
 * Login 버튼 누르면 RealtorDialog 로 이동
 */
class RealtorLoginFragment : Fragment() {
    private var _binding: FragmentRealtorLoginBinding? = null
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
        _binding = FragmentRealtorLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

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
                    userPw,
                    PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_FCM)!!
                )
            )
            .setListener {
                showToast(it.message)
                if (it.isSuccess) {
                    PreferenceUtil(requireContext())
                        .init().start()
                        .setString(PREFERENCE_REALTOR_ID, userId)
                        .setString(PREFERENCE_REALTOR_PW, userPw)

                    println(PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_ID)!!)
                    println(PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_PW)!!)

                    requireActivity().startActivity<RealtorAwaitingActivity> {
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