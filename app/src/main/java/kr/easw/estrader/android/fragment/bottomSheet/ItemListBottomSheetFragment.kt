package kr.easw.estrader.android.fragment.bottomSheet

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.FragmentBottomSheetBinding
import kr.easw.estrader.android.fragment.viewBinding


class ItemListBottomSheetFragment(val mContext : Context) : BottomSheetDialogFragment() {
    // onCreateView()에서 뷰가 이미 inflate 해서 ::bind 사용
    private val binding by viewBinding(FragmentBottomSheetBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }
}