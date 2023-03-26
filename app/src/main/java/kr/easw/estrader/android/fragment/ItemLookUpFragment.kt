package kr.easw.estrader.android.fragment

import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity
import kr.easw.estrader.android.databinding.FragmentItemlookupBinding
import kr.easw.estrader.android.dialog.AwaitingBidDialog

/**
 * 사용자 전용 부동산 매각 상세정보 Fragment
 * 대리위임 버튼 클릭 시 "대리 위임 동의" 팝업 확인 후, AwaitingBidDialog 로 이동
 */

class ItemLookUpFragment : Fragment() {
    private var _binding: FragmentItemlookupBinding? = null
    private val binding get() = _binding!!
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var delegate: Button
    private lateinit var cancle: Button
    private lateinit var toolbar: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemlookupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        initFields()

        // CollapsingToolbarLayout 가 축소할 때만 Toolbar 에 제목 표시
        onOffTitleAppBar()

        // "대리 위임 동의" 팝업 확인 후, AwaitingBidDialog 로 이동
        delegate.setOnClickListener {
            delegateAccept()
        }

        // "대리 위임 동의" 팝업 취소 이벤트 처리
        cancle.setOnClickListener {
            delegateReject()
        }

        // 툴바 navigationIcon 클릭 이벤트 처리
        toolbar.setNavigationOnClickListener {
            toolbarNavClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFields(){
        collapsingToolbarLayout = binding.collapsingLayout
        appBarLayout = binding.appbarLayout
        delegate = binding.confirmButton2
        cancle = binding.confirmButton
        toolbar = binding.toolbar
    }

    private fun delegateAccept() {
        AlertDialog.Builder(requireContext(), R.style.AppTheme_AlertDialogTheme)
            .setTitle("대리 위임 동의")
            .setMessage("대리 위임을 신청하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                startActivity(
                    Intent(requireContext(), AwaitingBidDialog::class.java)
                )
            }
            .setNegativeButton("취소") { _, _ ->
            }
            .create()
            .show()
    }

    private fun delegateReject() {
        startActivity(Intent(requireContext(), MainListActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NO_HISTORY
        })
        requireActivity().finish()
    }

    private fun toolbarNavClick() {
        startActivity(Intent(requireContext(), MainListActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NO_HISTORY
        })
        requireActivity().finish()
    }

    private fun onOffTitleAppBar() {
        appBarLayout.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.title = "Title"
                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "
                    isShow = false
                }
            }
        })
    }
}