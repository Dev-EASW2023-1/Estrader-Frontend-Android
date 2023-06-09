package kr.easw.estrader.android.fragment.delegation.user

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kr.easw.estrader.android.activity.user.MainListActivity
import kr.easw.estrader.android.databinding.FragmentRealtorSuccessDelegationBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.ContractRequest

/**
 * 대리위임 신청 완료 Dialog
 * 확인 누르면 "전 화면으로 돌아갑니다." 팝업 후, MainListActivity 이동
 */
class SuccessDelegationFragment : Fragment() {
    private var _binding: FragmentRealtorSuccessDelegationBinding? = null
    private val binding get() = _binding!!
    private val alertBtn: FloatingActionButton by lazy {
        binding.confirmButton
    }
    private val realtorName: TextView by lazy {
        binding.realtorName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRealtorSuccessDelegationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initFields()

        contractComplete()

        alertBtn.setOnClickListener {
            alertClick()
        }
    }
    private fun initFields() {
        alertBtn
        realtorName
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
                realtorName.text = it.name
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