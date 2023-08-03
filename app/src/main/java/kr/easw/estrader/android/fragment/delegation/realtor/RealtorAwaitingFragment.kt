package kr.easw.estrader.android.fragment.delegation.realtor

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.FragmentRealtorAwaitingBinding


class RealtorAwaitingFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentRealtorAwaitingBinding? = null
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
        _binding = FragmentRealtorAwaitingBinding.inflate(inflater, container, false)
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
//                requireActivity().startActivity<RealtorMainListActivity> {
//                    flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                }
//                requireActivity().finish()

                showBottomSheetDialog()

            }
        }
    }

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.SlowBottomSheetDialog)
        bottomSheetDialog.window?.attributes?.windowAnimations = R.style.SlowBottomSheetDialog
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout)

        val parentLayout = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        parentLayout?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            it.background = ContextCompat.getDrawable(it.context, R.drawable.rounded_dialog)
        }

        bottomSheetDialog.show()
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