package kr.easw.estrader.android.fragment


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import kr.easw.estrader.android.databinding.FragmentDelegateitemBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_ID
import kr.easw.estrader.android.definitions.PREFERENCE_PICTURE_URL
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_ID
import kr.easw.estrader.android.model.dto.ItemInContractDto
import kr.easw.estrader.android.util.PreferenceUtil

class DelegateItemFragment : Fragment() {
    private var _binding: FragmentDelegateitemBinding? = null
    private val binding get() = _binding!!
    private val casenumber : TextView by lazy {
        binding.casenumber
    }
    private val reserveprice : TextView by lazy {
        binding.reserveprice
    }
    private val location: TextView by lazy {
        binding.location
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDelegateitemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        initFields()

        showItem()

        Glide.with(binding.mainimage)
            .load(PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_PICTURE_URL)!!)
            .into(binding.mainimage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFields() {
        casenumber
        reserveprice
        location
    }

    private fun showItem() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.GET_CONTRACT_ITEM
            .setRequestParams(
                ItemInContractDto(
                    PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_ID)!!,
                    PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_ID)!!,
                    PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_PICTURE_URL)!!
                )
            )
            .setListener {
                casenumber.text = it.itemDto[0].information
                reserveprice.text = it.itemDto[0].reserveprice
                location.text = it.itemDto[0].location
                dialog.dismiss()
            }

            .build(requireContext())
    }
}