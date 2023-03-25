package kr.easw.estrader.android.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.MainListActivity
import kr.easw.estrader.android.databinding.FragmentItemlookupBinding
import kr.easw.estrader.android.dialog.AwaitingbidDialog

/**
 * Item 상세 정보 Fragment
 * 대리위임버튼 클릭하면 확인 팝업 후 AwaitingbidDialog로 이동
 */

class ItemLookUpFragment : Fragment() {
    private var _binding: FragmentItemlookupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemlookupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        onOffTitleAppBar()

        binding.confirmButton2.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(), R.style.AppTheme_AlertDialogTheme)
            builder.setTitle("대리위임 동의")
            builder.setMessage("대리위임을 신청 하시겠습니까?")
            builder.setPositiveButton("확인") { _, _ ->
                accept()
            }
            builder.setNegativeButton("취소") { _, _ ->

            }
            builder.show()
        }

        binding.confirmButton.setOnClickListener {
            val intent = Intent(requireContext(), MainListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
            activity?.finish()
        }
        val toolbar = view.findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        toolbar.setNavigationOnClickListener {
            // 툴바 navigationIcon 클릭 이벤트 처리
            val intent = Intent(requireContext(), MainListActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            startActivity(intent)
            activity?.finish()
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onOffTitleAppBar() {
        val collapsingToolbarLayout = binding.collapsingLayout
        val appBarLayout = binding.appbarLayout
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

    private fun accept() {
        // MainListActivity로 이동
        val intent = Intent(requireContext(), AwaitingbidDialog::class.java)
        startActivity(intent)

    }
}