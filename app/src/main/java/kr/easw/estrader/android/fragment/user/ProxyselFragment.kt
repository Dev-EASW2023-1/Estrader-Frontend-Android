package kr.easw.estrader.android.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.FragmentProxyselBinding

class ProxyselFragment : Fragment() {
    private var _binding: FragmentProxyselBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProxyselBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auction_period = view?.findViewById<TextView>(R.id.auction_period)

        auction_period?.setOnClickListener {
            val realtorInfoFragment = RealtorInfoFragment()

            parentFragmentManager.beginTransaction().apply {
                replace(
                    R.id.fragment_container,
                    realtorInfoFragment
                )
                addToBackStack(null)
                commit()
            }
        }


    }


}