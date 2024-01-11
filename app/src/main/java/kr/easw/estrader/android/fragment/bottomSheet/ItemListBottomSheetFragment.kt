package kr.easw.estrader.android.fragment.bottomSheet

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.easw.estrader.android.databinding.FragmentItemlookupBinding
import kr.easw.estrader.android.fragment.viewBinding


class ItemListBottomSheetFragment(val mContext: Context) : BottomSheetDialogFragment() {
    // onCreateView()에서 뷰가 이미 inflate 해서 ::bind 사용
    private val binding by viewBinding(FragmentItemlookupBinding::bind)

    companion object {
        private const val ARG_POSITION = "position"

        fun indexImage(context: Context, iconDrawable: String): ItemListBottomSheetFragment {
            val fragment = ItemListBottomSheetFragment(context)
            fragment.arguments = bundleOf(ARG_POSITION to iconDrawable)
            return fragment
        }
      }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentItemlookupBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            val bottomSheetDialog = it as BottomSheetDialog
            val bottomSheet = bottomSheetDialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

            val displayMetrics = context?.resources?.displayMetrics
            val windowHeight = displayMetrics?.heightPixels
            bottomSheet?.layoutParams?.height = windowHeight?.times(0.9)?.toInt() ?: FrameLayout.LayoutParams.WRAP_CONTENT
        }

        return dialog
    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as? FrameLayout
        val behavior = bottomSheet?.let { BottomSheetBehavior.from(it) }

        behavior?.apply {
            // 화면 높이의 90%를 계산하여 최대 높이 설정
            val displayMetrics = mContext.resources.displayMetrics
            val windowHeight = displayMetrics.heightPixels
            peekHeight = windowHeight.times(0.9).toInt()

            // 바텀 시트가 처음에 전체 높이로 열리도록 설정
            state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(binding.mainimage)
            .load(arguments?.getString(ARG_POSITION))
            .into(binding.mainimage)

        binding.confirmButton2.setOnClickListener {
            Log.d("fdfsdsf", "fdsfsdfs")

        }
    }

}