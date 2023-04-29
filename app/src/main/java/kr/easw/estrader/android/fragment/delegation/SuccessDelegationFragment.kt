package kr.easw.estrader.android.fragment.delegation

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.user.MainListActivity
import kr.easw.estrader.android.databinding.FragmentSuccessfuldelegationBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.ContractRequest

/**
 * 대리위임 신청 완료 Dialog
 * 확인 누르면 "전 화면으로 돌아갑니다." 팝업 후, MainListActivity 이동
 */
class SuccessDelegationFragment : Fragment() {
    private var _binding: FragmentSuccessfuldelegationBinding? = null
    private val binding get() = _binding!!
    private lateinit var alertBtn: Button
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuccessfuldelegationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        alertBtn = requireActivity().findViewById(R.id.confirm_button)

        contractComplete()

        alertBtn.setOnClickListener {
            alertClick()
        }
    }

    private fun alertClick() {
        AlertDialog.Builder(requireContext())
            .setMessage("전 화면으로 돌아갑니다.")
            .setPositiveButton("확인") { _, _ ->
                requireActivity().startActivity<MainListActivity> {
                    flags = FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP
                }
                requireActivity().finish()
            }
            .create()
            .show()
    }

    private fun contractComplete(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.CONTRACT_COMPLETE
            .setRequestParams(
                ContractRequest(
                    arguments?.getString("userId")!!,
                    arguments?.getString("targetId")!!,
                    arguments?.getString("itemImage")!!,
                    "3",
                    "제목",
                    "내용"
                )
            )
            .setListener {
                showToast(it.message)
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