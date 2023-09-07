package kr.easw.estrader.android.fragment.delegation.realtor

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
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
import androidx.recyclerview.widget.ItemTouchHelper
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
import kr.easw.estrader.android.definitions.PREFERENCE_REALTOR_TOKEN
import kr.easw.estrader.android.extensions.startActivity
import kr.easw.estrader.android.model.data.NotificationHolder
import kr.easw.estrader.android.model.dto.FcmRequest
import kr.easw.estrader.android.model.dto.NotificationItem
import kr.easw.estrader.android.util.PreferenceUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.annotations.Nullable
import java.lang.ref.WeakReference


class RealtorAwaitingFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentRealtorAwaitingBinding? = null
    private val binding get() = _binding!!

    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var fabHome: FloatingActionButton
    private var currentDx = 0f
    private var clamp = 0f
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
    private val recyclerViewAdapter by lazy { // 디자인
        object : RecyclerView.Adapter<NotificationHolder>() {
            override fun onCreateViewHolder( // 껍데기
                parent: ViewGroup, viewType: Int
            ): NotificationHolder {
                recyclerBinding = ElementRealtorAwaitingBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return NotificationHolder(recyclerBinding, itemClickListener?.get())
            }

            override fun onBindViewHolder( // 껍데기랑 datalist 연결
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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRealtorAwaitingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
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
        when (v.id) {
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
        val offset = 20
        val itemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.bottom = offset
            }
        }

        val defaultItemAnimator = DefaultItemAnimator().apply {
            addDuration = 1000
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
            itemAnimator = defaultItemAnimator
            addItemDecoration(itemDecoration)
        }

        val simpleItemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            // 스와이프 임계값 설정
            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 0.5f
            }
            // 스와이프 할때 나오는 버튼
            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val view = getView(viewHolder)
                    val isClamped = getTag(viewHolder)
                    val x = clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)
                    currentDx = x
                    getDefaultUIUtil().onDraw(
                        c, recyclerView, view, x, dY, actionState, isCurrentlyActive
                    )

                    when {
                        dX > 0 ->drawButton(c, viewHolder, ButtonDirection.LEFT)
                        dX < 0 -> drawButton(c, viewHolder, ButtonDirection.RIGHT, context)

                    }
                }
            }





            var removedItem: NotificationItem? = null // 저장할 항목의 타입을 NotificationItem으로 변경

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    binding.recyclerView.adapter?.notifyItemChanged(position)
                    showBottomSheetDialog(position)
                } else if (direction == ItemTouchHelper.RIGHT) {
                    removedItem = dataList[position] // 항목 임시 저장
                    dataList.removeAt(position)
                    recyclerViewAdapter.notifyItemRemoved(position)
                }
            }
        }

        // 스와이프 동작을 리사이클러뷰에 연결
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        // recyclerView 아이템 클릭 이벤트 설정
        recyclerViewAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                // TODO("아직 정하지 않았음.")
                showBottomSheetDialog(position)
            }
        })
    }




    private fun acceptDelegation(position: Int) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(ProgressBar(requireContext()))
        dialog.show()

        ApiDefinition.REALTOR_SEND_FCM.setRequestParams(
            FcmRequest(
                PreferenceUtil(requireContext()).init().start()
                    .getString(PREFERENCE_REALTOR_ID)!!,
                dataList[position].targetId,
                dataList[position].itemImage,
                "2",
                "제목",
                "내용"
            )
        ).setListener {
            showToast(it.message)
            dialog.dismiss()
        }
        .setRequestHeaders(mutableMapOf("Authorization" to "Bearer " + PreferenceUtil(requireContext()).init().start().getString(PREFERENCE_REALTOR_TOKEN)!!))
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
        if (!isFCMReceived) {
            isFCMReceived = true
            loadingImageView.visibility = View.GONE
            infoText1.text = getString(R.string.notificationReceived)
            infoText2.visibility = View.GONE
            background.setBackgroundResource(R.drawable.realtor_awaiting_gradient)
        }
    }

    // 텍스트 삽입
    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize = 60f
        p.color = Color.WHITE
        p.isAntiAlias = true
        p.textSize = textSize
        val textWidth = p.measureText(text)
        c.drawText(
            text, button.centerX() - textWidth / 2, button.centerY() + textSize / 2, p
        )
    }
    private fun drawDeleteIcon(c: Canvas, button: RectF, context: Context) {
        // 이미지 크기 조절 (1은 원본 크기)
        val options = BitmapFactory.Options()
        options.inSampleSize = 1

        // 아이콘 불러옴
        val deleteIcon: Bitmap = BitmapFactory.decodeResource(
            context.resources, R.drawable.tmfprl
        )

        val iconLeft = button.centerX() - (deleteIcon.width / 2)
        val iconTop = button.centerY() - (deleteIcon.height / 2)
        val iconRight = button.centerX() + (deleteIcon.width / 2)
        val iconBottom = button.centerY() + (deleteIcon.height / 2)


        c.drawBitmap(deleteIcon, iconLeft, iconTop, null)
    }

    private fun drawButton(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        direction: ButtonDirection,
        context: Context? = null
    ) {
        println(":) | drawButton $direction")
        val buttonWidthWithoutPadding = requireView().width.toFloat() / 6.5
        println(":) | drawButton $buttonWidthWithoutPadding")
        println(":) | drawButton ${requireView().width.toFloat()}")
        val corners = 35f
        val itemView = viewHolder.itemView
        val p = Paint()

        val button = when (direction) {
            ButtonDirection.LEFT -> {
                p.color = Color.RED
                RectF(
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    (itemView.left + buttonWidthWithoutPadding).toFloat(),
                    itemView.bottom.toFloat()
                )
            }
            ButtonDirection.RIGHT -> {
                p.color = Color.BLUE
                RectF(
                    (itemView.right - buttonWidthWithoutPadding).toFloat(),
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
            }
        }
        c.drawRoundRect(button, corners, corners, p)

        when (direction) {
            ButtonDirection.RIGHT -> drawText("DELETE", c, button, p)
            ButtonDirection.LEFT -> drawText("수락", c, button, p)
        }
    }

    private enum class ButtonDirection {
        LEFT, RIGHT
    }

    private fun clampViewPositionHorizontal(
        view: View, dX: Float, isClamped: Boolean, isCurrentlyActive: Boolean
    ): Float {
        val min = -view.width.toFloat() / 5  // 왼쪽으로 스와이프 가능한 최대 범위
        val max = view.width.toFloat() / 5   // 오른쪽으로 스와이프 가능한 최대 범위

        val x = if (isClamped) {
            when {
                isCurrentlyActive && dX < 0 -> dX - clamp
                isCurrentlyActive && dX > 0 -> dX + clamp
                dX < 0 -> -clamp
                else -> clamp
            }
        } else {
            dX
        }

        return x.coerceIn(min, max)
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder): Boolean {
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    @Nullable
    private fun getView(viewHolder: RecyclerView.ViewHolder): View {
        return viewHolder.itemView
    }


    private var removedItem: NotificationItem? = null  // 클래스의 멤버 변수로 선언

    private fun showBottomSheetDialog(position: Int) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout)

        val confirmButton = bottomSheetDialog.findViewById<Button>(R.id.confirm_button)
        val rejectButton = bottomSheetDialog.findViewById<Button>(R.id.reject_button)

        confirmButton?.setOnClickListener {
            acceptDelegation(position)
            bottomSheetDialog.dismiss()
        }

        rejectButton?.setOnClickListener {
            removedItem = dataList[position] // 항목 임시 저장
            dataList.removeAt(position)  // 항목 제거
            recyclerViewAdapter.notifyItemRemoved(position)
            bottomSheetDialog.dismiss()
        }

        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetDialog.show()
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