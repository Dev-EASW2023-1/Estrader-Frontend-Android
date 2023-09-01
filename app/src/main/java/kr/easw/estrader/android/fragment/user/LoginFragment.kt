package kr.easw.estrader.android.fragment.user

import android.Manifest
import android.app.Dialog
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.user.MainListActivity
import kr.easw.estrader.android.databinding.FragmentLoginBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_FCM
import kr.easw.estrader.android.definitions.PREFERENCE_ID
import kr.easw.estrader.android.definitions.PREFERENCE_PW
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.SignInRequest
import kr.easw.estrader.android.util.PreferenceUtil

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        checkAndRequestPermissions()
    }

    private fun setupViews() {
        binding.signIn.setOnClickListener {
            navigateToSignupFragment()
        }

        binding.loginNext.setOnClickListener {
            showUserIdInput()
        }

        binding.btnNext2.setOnClickListener {
            val inputId = binding.userId.editText?.text.toString()
            if (inputId.isNotEmpty()) {
                showUserPasswordInput()
            }
        }

        binding.btnNext.setOnClickListener {
            loginUser()
        }
    }

    private fun checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), YOUR_PERMISSION_REQUEST_CODE)
        }
    }

    private fun navigateToSignupFragment() {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.container_view, SignupFragment()).addToBackStack(null).commit()
    }

    private fun showUserIdInput() {
        binding.apply {
            welcom.visibility = View.GONE
            welcom?.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_left))
            userId.visibility = View.VISIBLE
            userId.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right))
        }
    }

    private fun showUserPasswordInput() {
        binding.apply {
            userPw.visibility = View.VISIBLE
            btnNext.visibility = View.VISIBLE

            btnNext2.visibility = View.GONE
            userId.visibility = View.GONE

            userPw?.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right))
            btnNext.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right))
        }
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
            SignInRequest(inputId, inputPw, PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_FCM)!!)
        ).setListener {
            showToast(it.message)
            if (it.isSuccess) {
                PreferenceUtil(requireContext()).init().start()
                    .setString(PREFERENCE_ID, inputId)
                    .setString(PREFERENCE_PW, inputPw)

                requireActivity().startActivity<MainListActivity> {
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

    companion object {
        private const val YOUR_PERMISSION_REQUEST_CODE = 1001
    }
}
