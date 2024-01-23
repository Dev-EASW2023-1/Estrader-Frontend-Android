package kr.easw.estrader.android.fragment.user

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
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
import kr.easw.estrader.android.activity.user.AwaitingActivity
import kr.easw.estrader.android.databinding.FragmentItemlookupBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_ID
import kr.easw.estrader.android.definitions.PREFERENCE_TOKEN
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.FcmRequest
import kr.easw.estrader.android.model.dto.LookUpItemRequest
import kr.easw.estrader.android.util.PreferenceUtil
import java.nio.charset.Charset

/**
 * 사용자 전용 부동산 매각 상세정보 Fragment
 * 대리위임 버튼 클릭 시 "대리 위임 동의" 팝업 확인 후, AwaitingBidDialog 로 이동
 */

class ItemLookUpFragment : Fragment() {
    private var _binding: FragmentItemlookupBinding? = null
    private val binding get() = _binding!!
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var toolbar: Toolbar
    private var toolbarTitle = " "
    private val delegate: Button by lazy {
        binding.confirmButton2
    }
    private val cancle: Button by lazy {
        binding.confirmButton
    }
    private val caseNumber : TextView by lazy {
        binding.caseNumber
    }
    private val itemType: TextView by lazy {
        binding.itemType
    }
    private val location: TextView by lazy {
        binding.location
    }
    private val reservePrice : TextView by lazy {
        binding.reservePrice
    }
    private val note: TextView by lazy {
        binding.note
    }

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

        showItem()

        delegate.setOnClickListener {
            delegateAccept()
        }
        cancle.setOnClickListener {
            delegateReject()
        }

        binding.site.setOnClickListener {
            siteurl()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initFields() {
        collapsingToolbarLayout = binding.collapsingLayout
        appBarLayout = binding.appbarLayout
//        toolbar = binding.toolbar
        delegate
        cancle
        caseNumber
        itemType
        location
        reservePrice
        note
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
                            "제목",
                            "내용"
                        )
                    )
                    .setListener {
                        showToast(it.message)
                        dialog.dismiss()
                        if(it.isSuccess){
                            requireActivity().startActivity<AwaitingActivity> {
                                flags = FLAG_ACTIVITY_SINGLE_TOP or FLAG_ACTIVITY_CLEAR_TOP
                            }
                        }
                    }
                    .setRequestHeaders(mutableMapOf("Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_TOKEN)!!))
                    .build(requireContext())
            }
            .setNegativeButton("취소") { _, _ ->
            }
            .create()
            .show()
    }

    private fun startWebViewDialogBtn(newUrl : String) {
        val url = newUrl
        val mWebView = MyWebView(requireContext(), url)
        Log.d("url",url)
        val webViewDialog = Dialog(requireContext()).apply {

            window?.attributes?.apply {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            setContentView(mWebView)

            setOnKeyListener { _, keyCode, _ ->
                if(keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                    mWebView.goBack()
                    true
                } else {
                    false
                }
            }
        }

        webViewDialog.show()
    }
    fun modifyUrl(originalUrl: String, jiwonNm: String, caseNumber: String): String {
        val eucKrCharset = Charset.forName("EUC-KR")
        val encodedJiwonNm = jiwonNm.toByteArray(eucKrCharset)
            .joinToString(separator = "") { "%${it.toInt().and(0xff).toString(16).uppercase()}" }

        val (saYear, saSer) = caseNumber.split("타경").let { it[0].trim() to it[1].trim() }

        val urlParts = originalUrl.split("&").toMutableList()
        urlParts[0] = urlParts[0].substringBefore("jiwonNm=") + "jiwonNm=$encodedJiwonNm"
        urlParts[1] = "saYear=$saYear"
        urlParts[2] = "saSer=$saSer"

        return urlParts.joinToString("&")
    }


    private fun siteurl() {
        val originalUrl = "https://www.courtauction.go.kr/RetrieveRealEstDetailInqSaList.laf?jiwonNm=&saYear=&saSer=&_CUR_SRNID=PNO102014&_CUR_CMD=InitMulSrch.laf&_SRCH_SRNID=PNO102014&_NEXT_CMD=RetrieveRealEstDetailInqSaList.laf"
        val jiwonNmValue = "의정부지방법원"
        val textViewText = "2022타경 77053"

        val newUrl = modifyUrl(originalUrl, jiwonNmValue, textViewText)
        startWebViewDialogBtn(newUrl)

    }
    private fun showItem() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.SHOW_ITEM
            .setRequestParams(
                LookUpItemRequest(
                    arguments?.getString(ARG_POSITION).toString()
                )
            )
            .setListener {
                caseNumber.text = it.caseNumber
                itemType.text = it.itemType
                location.text = it.location
                reservePrice.text = it.minimumBidPrice
                note.text = it.note
                toolbarTitle = it.caseNumber
                dialog.dismiss()
            }
            .setRequestHeaders(mutableMapOf("Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_TOKEN)!!))
            .build(requireContext())
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun delegateReject() {
        requireActivity().supportFragmentManager.commit {
            replace(R.id.framelayout, MapViewFragment())
        }
    }

    private fun toolbarNavClick() {
        requireActivity().supportFragmentManager.commit {
            replace(R.id.framelayout, MapViewFragment())
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
                    collapsingToolbarLayout.title = toolbarTitle
                    isShow = true
                } else if (isShow) {
                    collapsingToolbarLayout.title = " "
                    isShow = false
                }
            }
        })
    }
}