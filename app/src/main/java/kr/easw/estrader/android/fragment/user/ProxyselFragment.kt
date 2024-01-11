package kr.easw.estrader.android.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.databinding.FragmentEnterPriceBinding
import kr.easw.estrader.android.databinding.FragmentProxyselBinding

class ProxyselFragment : Fragment(){
    private var _binding: FragmentProxyselBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProxyselBinding.inflate(inflater, container, false)
        return binding.root
    }

}