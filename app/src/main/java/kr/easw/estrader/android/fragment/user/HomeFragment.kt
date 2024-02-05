package kr.easw.estrader.android.fragment.user

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.FragmentHomeBinding
import kr.easw.estrader.android.fragment.BaseFragment
import kr.easw.estrader.android.fragment.delegation.user.ItemListFragment

class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressCardView.setOnClickListener {
            this.parentFragmentManager.commit {
                replace(R.id.container_view, ItemListFragment())
            }
        }
    }
}