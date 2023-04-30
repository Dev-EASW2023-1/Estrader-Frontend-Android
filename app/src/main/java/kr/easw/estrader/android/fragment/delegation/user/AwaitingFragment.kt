package kr.easw.estrader.android.fragment.delegation.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.user.MainListActivity
import kr.easw.estrader.android.databinding.FragmentAwaitingBinding
import kr.easw.estrader.android.extensions.startActivity

class AwaitingFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentAwaitingBinding? = null
    private val binding get() = _binding!!

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabHome: FloatingActionButton

    private var isOpenFlag = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAwaitingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)
        fabAdd = binding.fab
        fabHome = binding.fabHome

        fabAdd.setOnClickListener(this)
        fabHome.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when(v.id){
            binding.fab.id -> anim()
            binding.fabHome.id -> {
                requireActivity().startActivity<MainListActivity> {
                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                requireActivity().finish()
            }
        }
    }

    private fun anim() {
        if (isOpenFlag) {
            fabHome.visibility = View.GONE
            fabHome.startAnimation(fabClose)

            fabHome.isClickable = false
            isOpenFlag = false

        } else {
            fabHome.visibility = View.VISIBLE
            fabHome.startAnimation(fabOpen)

            fabHome.isClickable = true
            isOpenFlag = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}