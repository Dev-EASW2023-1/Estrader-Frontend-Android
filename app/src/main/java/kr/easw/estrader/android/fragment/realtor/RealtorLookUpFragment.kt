package kr.easw.estrader.android.fragment.realtor

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kr.easw.estrader.android.activity.realtor.RealtorAwaitingActivity
import kr.easw.estrader.android.databinding.FragmentRealtorItemlookupBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.ItemInContractDto

/**
 * 대리인 전용 부동산 매각 상세정보 Fragment
 * 대리 위임 계약 체결 완료 후 물건 정보 확인 가능
 */
class RealtorLookUpFragment : Fragment() {
    private var _binding: FragmentRealtorItemlookupBinding? = null
    private val binding get() = _binding!!
    private val backButton: Button by lazy {
        binding.confirmButton
    }
    private val caseNumber : TextView by lazy {
        binding.caseNumber
    }
    private val itemType: TextView by lazy {
        binding.itemType
    }
    private val location: TextView by lazy {
        binding.location
    }
    private val reservePrice : TextView by lazy {
        binding.reservePrice
    }
    private val note: TextView by lazy {
        binding.note
    }

    companion object {
        fun setArguments(userId: String, targetId: String, itemImage: String) = RealtorLookUpFragment().apply {
            arguments = bundleOf(
                "userId" to userId,
                "targetId" to targetId,
                "itemImage" to itemImage
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRealtorItemlookupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        initFields()

        showItem()

        backButton.setOnClickListener {
            requireActivity().startActivity<RealtorAwaitingActivity> {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            requireActivity().finish()
        }

        Glide.with(binding.mainimage)
            .load(arguments?.getString("itemImage").toString())
            .into(binding.mainimage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFields() {
        backButton
        caseNumber
        itemType
        location
        reservePrice
        note
    }

    private fun showItem() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.GET_CONTRACT_ITEM
            .setRequestParams(
                ItemInContractDto(
                    arguments?.getString("userId").toString(),
                    arguments?.getString("targetId").toString(),
                    arguments?.getString("itemImage").toString()
                )
            )
            .setListener {
                caseNumber.text = it.caseNumber
                itemType.text = it.itemType
                location.text = it.location
                reservePrice.text = it.minimumBidPrice
                note.text = it.note
                dialog.dismiss()
            }

            .build(requireContext())
    }
}