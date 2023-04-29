package kr.easw.estrader.android.fragment.delegation

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.FragmentRealtormatchBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_ID
import kr.easw.estrader.android.extensions.replaceFragment
import kr.easw.estrader.android.model.dto.FcmRequest
import kr.easw.estrader.android.model.dto.LookUpItemRequest
import kr.easw.estrader.android.util.PreferenceUtil

class RealtorMatchFragment : Fragment() {
    private var _binding: FragmentRealtormatchBinding? = null
    private val binding get() = _binding!!
    private val alertBtn: Button by lazy {
        binding.confirmButton
    }
    private val auctionHouse : TextView by lazy {
        binding.auctionHouse
    }
    private val caseNumber : TextView by lazy {
        binding.caseNumber
    }
    private val reservePrice : TextView by lazy {
        binding.reservePrice
    }
    private val auctionPeriod: TextView by lazy {
        binding.auctionPeriod
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRealtormatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initFields()

        showItem()

        alertBtn.setOnClickListener {
            alertClick()
        }
    }

    private fun initFields() {
        alertBtn
        auctionHouse
        caseNumber
        reservePrice
        auctionPeriod
    }

    private fun alertClick() {
        AlertDialog.Builder(requireContext())
            .setMessage("입찰표 PDF를 출력합니다. 잠시 기다려주세요.")
            .setPositiveButton("확인") { _, _ ->
                val dialog = Dialog(requireContext())
                dialog.setContentView(ProgressBar(requireContext()))
                dialog.show()

                ApiDefinition.REALTOR_SEND_FCM
                    .setRequestParams(
                        FcmRequest(
                            PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_ID)!!,
                            arguments?.getString("targetId")!!,
                            arguments?.getString("itemImage")!!,
                            "2",
                            "제목",
                            "내용"
                        )
                    )
                    .setListener {
                        showToast(it.message)
                        if(it.isSuccess){
                            requireActivity().supportFragmentManager.replaceFragment<AwaitingFragment>(
                                R.id.container_view,
                                null
                            )
                        }
                        dialog.dismiss()
                    }
                    .build(requireContext())
            }
            .create()
            .show()
    }

    private fun showItem() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.REALTOR_SHOW_ITEM
            .setRequestParams(
                LookUpItemRequest(
                    arguments?.getString("itemImage")!!
                )
            )
            .setListener {
                auctionHouse.text = it.court
                caseNumber.text = it.caseNumber
                reservePrice.text = it.minimumBidPrice
                auctionPeriod.text = it.biddingPeriod.replace("\n", "")
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