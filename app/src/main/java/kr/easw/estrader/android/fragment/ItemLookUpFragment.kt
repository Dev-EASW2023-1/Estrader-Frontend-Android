package kr.easw.estrader.android.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.FragmentItemlookupBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_ID
import kr.easw.estrader.android.dialog.AwaitingBidDialog
import kr.easw.estrader.android.model.dto.FcmRequest
import kr.easw.estrader.android.util.PreferenceUtil

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

    companion object {
        private const val ARG_POSITION = "position"

        // MainListFragment 에서 데이터 받기 위해 bundleOf("키" to "값") 으로 bundle 생성
        fun indexImage(iconDrawable: String) = ItemLookUpFragment().apply {
            arguments = bundleOf(ARG_POSITION to iconDrawable)
        }
    }

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

        // MainListFragment 에서 보내준 이미지 URL 을 Glide 로 출력
        Glide.with(binding.mainimage)
            .load(arguments?.getString(ARG_POSITION).toString())
            .into(binding.mainimage)

        // "대리 위임 동의" 팝업 확인 후, AwaitingBidDialog 로 이동
        delegate.setOnClickListener {
            delegateAccept()
        }

        // "대리 위임 동의" 팝업 취소 이벤트 처리
        cancle.setOnClickListener {
            delegateReject()
        }

        // 툴바 navigationIcon 클릭 이벤트 처리
        binding.toolbar.setNavigationOnClickListener {
            toolbarNavClick()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFields() {
        collapsingToolbarLayout = binding.collapsingLayout
        appBarLayout = binding.appbarLayout
        delegate = binding.confirmButton2
        cancle = binding.confirmButton
        toolbar = binding.toolbar
    }

    private fun delegateAccept() {
        AlertDialog.Builder(requireContext())
            .setTitle("대리 위임 동의")
            .setMessage("대리 위임을 신청하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                val dialog = Dialog(requireContext())
                dialog.setContentView(ProgressBar(requireContext()))
                dialog.show()

                ApiDefinition.SEND_FCM
                    .setRequestParams(
                        FcmRequest(
                            PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_ID)!!,
                            "test4",
                            arguments?.getString(ARG_POSITION).toString(),
                            "1",
                            "안녕",
                            "난 야옹이야~"
                        )
                    )
                    .setListener {
                        showToast(it.message)
                        dialog.dismiss()

                        startActivity(
                            Intent(requireContext(), AwaitingBidDialog::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            }
                        )
                    }
                    .build(requireContext())
            }
            .setNegativeButton("취소") { _, _ ->
            }
            .create()
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun delegateReject() {
        requireActivity().supportFragmentManager.commit {
            replace(R.id.framelayout, MainListFragment())
        }
    }

    private fun toolbarNavClick() {
        requireActivity().supportFragmentManager.commit {
            replace(R.id.framelayout, MainListFragment())
        }
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