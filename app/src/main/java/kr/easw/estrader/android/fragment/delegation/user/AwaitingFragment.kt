package kr.easw.estrader.android.fragment.delegation.user

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.user.MainListActivity
import kr.easw.estrader.android.databinding.FragmentAwaitingBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_TOKEN
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.dto.ContractRequest
import kr.easw.estrader.android.model.dto.NotificationItem
import kr.easw.estrader.android.util.PreferenceUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AwaitingFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentAwaitingBinding? = null
    private val binding get() = _binding!!

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabHome: FloatingActionButton

    private var isOpenFlag = false
    private var isFCMReceived = false

    private val loadingImageView: ImageView by lazy {
        binding.loadingImageview
    }
    private val completeImageView: ImageView by lazy {
        binding.completeImageview
    }
    private val realtorName: TextView by lazy {
        binding.realtorName
    }
    private val infoText1: TextView by lazy {
        binding.showInfoText1
    }
    private val infoText2: TextView by lazy {
        binding.showInfoText2
    }
    private val background: ConstraintLayout by lazy {
        binding.background
    }

    private var dataList: MutableList<NotificationItem> = mutableListOf()

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

        initFields()
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

    private fun initFields() {
        loadingImageView
        completeImageView
        realtorName
        infoText1
        infoText2
        background
    }

    private fun contractComplete(){
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.CONTRACT_COMPLETE
            .setRequestParams(
                ContractRequest(
                    dataList[0].userId,
                    dataList[0].targetId,
                    dataList[0].itemImage,
                    "3",
                    "제목",
                    "내용"
                )
            )
            .setListener {
                showToast(it.message)
                // FCM 이벤트 수신 시 특정 View 숨기는 로직
                if(!isFCMReceived) {
                    isFCMReceived = true
                    loadingImageView.visibility = View.GONE
                    completeImageView.visibility = View.VISIBLE
                    realtorName.text = it.name
                    infoText1.visibility = View.VISIBLE
                    infoText2.text = getString(R.string.contractCompleted)
                    background.setBackgroundResource(R.drawable.realtor_awaiting_gradient)
                }
                dialog.dismiss()
            }
            .setRequestHeaders(mutableMapOf("Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_TOKEN)!!))
            .build(requireContext())
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // FCM 이벤트 수신 시 이 콜백 메소드 실행
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(result: NotificationItem) {
        dataList.add(result)

        contractComplete()
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().register(this) // 이벤트 버스 등록
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this) // 이벤트 버스 해제
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}