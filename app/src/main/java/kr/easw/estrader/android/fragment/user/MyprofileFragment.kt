package kr.easw.estrader.android.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.abstracts.BasePagingAdapter
import kr.easw.estrader.android.databinding.ElementItemBinding
import kr.easw.estrader.android.databinding.ElementProfileBinding
import kr.easw.estrader.android.databinding.FragmentMyprofileBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.model.dto.MainItem

class MyprofileFragment : Fragment() {
    private var _binding: FragmentMyprofileBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagingAdapter: BasePagingAdapter<MainItem, ElementProfileBinding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyprofileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}