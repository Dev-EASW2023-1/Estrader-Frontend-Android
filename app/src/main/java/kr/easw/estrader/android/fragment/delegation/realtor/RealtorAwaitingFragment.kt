package kr.easw.estrader.android.fragment.delegation.realtor

import android.app.Dialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kr.easw.estrader.android.R
import kr.easw.estrader.android.activity.realtor.RealtorMainListActivity
import kr.easw.estrader.android.databinding.ElementRealtorAwaitingBinding
import kr.easw.estrader.android.databinding.FragmentRealtorAwaitingBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_ID
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.data.NotificationHolder
import kr.easw.estrader.android.model.dto.FcmRequest
import kr.easw.estrader.android.model.dto.NotificationItem
import kr.easw.estrader.android.util.PreferenceUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference


class RealtorAwaitingFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentRealtorAwaitingBinding? = null
    private val binding get() = _binding!!

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabHome: FloatingActionButton

    private var recyclerBinding: ElementRealtorAwaitingBinding? = null
    private var itemClickListener: WeakReference<OnItemClickListener>? = null
    private var isOpenFlag = false
    private var isFCMReceived = false

    private val loadingImageView: ImageView by lazy {
        binding.loadingImageview
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
    private val recyclerViewAdapter by lazy {
        object : RecyclerView.Adapter<NotificationHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): NotificationHolder {
                recyclerBinding = ElementRealtorAwaitingBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return NotificationHolder(recyclerBinding, itemClickListener?.get())
            }

            override fun onBindViewHolder(
                holder: NotificationHolder, position: Int
            ) {
                holder.bind(dataList[position])
            }

            override fun getItemCount(): Int = dataList.size

            // 리스너 객체 참조를 recyclerView Adapter 에 전달
            // 약한 참조를 위해 WeakReference 설정
            fun setOnItemClickListener(listener: OnItemClickListener) {
                itemClickListener = WeakReference(listener)
            }
        }
    }

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

        initFields()
        initRecycler()
    }

    // recyclerView 에 사용할 커스텀 리스너 interface 정의
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onClick(v: View) {
        when(v.id){
            binding.fab.id -> anim()
            binding.fabHome.id -> {
                requireActivity().startActivity<RealtorMainListActivity> {
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
        infoText1
        infoText2
        background
    }

    private fun initRecycler() {
        recyclerViewAdapter

        // xml 에서 그리는 구분선, 여백은 무겁기 때문에, item 간격을 그리는 ItemDecoration 사용
        val itemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                val offset = 20
                outRect.bottom = offset
            }
        }

        // Animation 설정
        val defaultItemAnimator = DefaultItemAnimator()
        defaultItemAnimator.addDuration = 1000

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerViewAdapter
            itemAnimator = defaultItemAnimator
            addItemDecoration(itemDecoration)
        }

        // recyclerView 아이템 클릭 이벤트 설정
        recyclerViewAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                // TODO("아직 정하지 않았음.")
                showBottomSheetDialog(position)
            }
        })
    }

    private fun showBottomSheetDialog(position: Int) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout)

        // Find the button by its ID
        val confirmButton = bottomSheetDialog.findViewById<Button>(R.id.confirm_button)
        val rejectButton = bottomSheetDialog.findViewById<Button>(R.id.reject_button)

        confirmButton?.setOnClickListener{
            acceptDelegation(position)
            bottomSheetDialog.dismiss()
        }

        rejectButton?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        // Set the dialog to full screen
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetDialog.show()
    }

    private fun acceptDelegation(position: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.REALTOR_SEND_FCM
            .setRequestParams(
                FcmRequest(
                    PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_ID)!!,
                    dataList[position].targetId,
                    dataList[position].itemImage,
                    "2",
                    "제목",
                    "내용"
                )
            )
            .setListener {
                showToast(it.message)
                dialog.dismiss()
            }
            .build(requireContext())
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // FCM 이벤트 수신 시 이 콜백 메소드 실행
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(result: NotificationItem) {
        dataList.add(result)
        recyclerViewAdapter.notifyItemInserted(dataList.size)

        // FCM 이벤트 수신 시 특정 View 숨기는 로직
        if(!isFCMReceived) {
            isFCMReceived = true
            loadingImageView.visibility = View.GONE
            infoText1.text = getString(R.string.notificationReceived)
            infoText2.visibility = View.GONE
            background.setBackgroundResource(R.drawable.realtor_awaiting_gradient)
        }
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