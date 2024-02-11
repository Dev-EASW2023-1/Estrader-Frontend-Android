package kr.easw.estrader.android.util

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kr.easw.estrader.android.R
import kr.easw.estrader.android.abstracts.BaseBottomSheetLayout
import kr.easw.estrader.android.abstracts.BasePagingAdapter
import kr.easw.estrader.android.abstracts.BaseRecyclerViewAdapter
import kr.easw.estrader.android.abstracts.Inflate
import kr.easw.estrader.android.abstracts.OnItemClickListener
import kr.easw.estrader.android.databinding.ElementItemBinding
import kr.easw.estrader.android.databinding.ElementTabItemBinding
import kr.easw.estrader.android.databinding.FragmentItemDetailBinding
import kr.easw.estrader.android.fragment.user.ItemDetailFragment
import kr.easw.estrader.android.fragment.user.ItemLookUpFragment
import kr.easw.estrader.android.model.data.bindMainItemToView
import kr.easw.estrader.android.model.dto.MainItem
import kr.easw.estrader.android.model.dto.TitleItem

object ViewUtil {
    fun setupAdapterForMapView(
        fragment: Fragment,
        dataList: MutableList<MainItem>
    ): BasePagingAdapter<MainItem, ElementItemBinding> {
        return object: BasePagingAdapter<MainItem, ElementItemBinding>(dataList) {
            override val setupItemBinding: Inflate<ElementItemBinding> get() = ElementItemBinding::inflate
            // recyclerView 아이템 클릭 이벤트 설정
            override val setOnItemClickListener: OnItemClickListener get() = object : OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Bundle 을 이용해 position 에 해당 이미지 URL 넘기기
                    fragment.requireActivity().supportFragmentManager.commit {
                        replace(
                            R.id.framelayout,
                            ItemLookUpFragment.indexImage(dataList[position].iconDrawable)
                        )
                    }
                }
            }
            override fun onBindItem(binding: ElementItemBinding, item: MainItem) {
                bindMainItemToView(fragment.requireContext(), binding, item)
            }
        }
    }

    fun setupNewItemListAdapter(
        fragment: Fragment,
        dataList: MutableList<MainItem>
    ) : BasePagingAdapter<MainItem, ElementItemBinding> {
        return object: BasePagingAdapter<MainItem, ElementItemBinding>(dataList) {
            override val setupItemBinding: Inflate<ElementItemBinding> get() = ElementItemBinding::inflate
            // recyclerView 아이템 클릭 이벤트 설정
            override val setOnItemClickListener: OnItemClickListener get() = object : OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Bundle 을 이용해 position 에 해당 이미지 URL 넘기기
                    fragment.requireActivity().supportFragmentManager.commit {
                        replace(
                            R.id.container_view,
                            ItemDetailFragment.indexImage(dataList[position].iconDrawable)
                        )
                    }
                }
            }
            override fun onBindItem(binding: ElementItemBinding, item: MainItem) {
                bindMainItemToView(fragment.requireContext(), binding, item)
            }
        }
    }

    fun setupDetailItemTabAdapter(
        fragment: Fragment,
        dataList: MutableList<TitleItem>,
        recyclerViewBinding: FragmentItemDetailBinding
    ) : BaseRecyclerViewAdapter<TitleItem, ElementTabItemBinding> {
        return object: BaseRecyclerViewAdapter<TitleItem, ElementTabItemBinding>(dataList) {
            var selectedTitlePosition = 0
            override val setupItemBinding: Inflate<ElementTabItemBinding> get() = ElementTabItemBinding::inflate
            override val setOnItemClickListener: OnItemClickListener
                get() = object: OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        selectedTitlePosition = position
                        setTitleSelected(selectedTitlePosition)
                        when(selectedTitlePosition) {
                            0 -> {
                                recyclerViewBinding.itemDetailScroll.scrollTo(0, recyclerViewBinding.itemBasicInfo.root.top)
                            }
                            1 -> {
                                recyclerViewBinding.itemDetailScroll.scrollTo(0, recyclerViewBinding.itemPhotos.root.top)
                            }
                        }
                    }
                }
            override fun onBindItem(binding: ElementTabItemBinding, item: TitleItem) {
                binding.titleText.text = item.titleName
                if(item.isSelected) {
                    binding.titleText.setTextColor(fragment.requireActivity().getColorCompat(R.color.color_main))
                } else {
                    binding.titleText.setTextColor(fragment.requireActivity().getColorCompat(R.color.color_sub))
                }
            }
            override fun setTitleSelected(position: Int) {
                for((index, data) in dataList.withIndex()) {
                    data.isSelected = position == index
                }
                notifyItemRangeChanged(0, dataList.size)
            }

            override fun triggerScrollEventOnPass() {
                val scrollBounds = Rect()
                recyclerViewBinding.itemDetailScroll.getHitRect(scrollBounds)

                recyclerViewBinding.itemDetailScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
                    if(recyclerViewBinding.itemBasicInfo.propertyHeader.getLocalVisibleRect(scrollBounds)) {
                        selectedTitlePosition = 0
                        setTitleSelected(selectedTitlePosition)
                    }

                    if(recyclerViewBinding.itemPhotos.propertyHeader.getLocalVisibleRect(scrollBounds)) {
                        selectedTitlePosition = 1
                        setTitleSelected(selectedTitlePosition)
                    }
                })
            }
        }
    }

    fun setupScheduledItemListAdapter(
        fragment: Fragment,
        dataList: MutableList<MainItem>
    ) : BasePagingAdapter<MainItem, ElementItemBinding> {
        return object: BasePagingAdapter<MainItem, ElementItemBinding>(dataList) {
            override val setupItemBinding: Inflate<ElementItemBinding> get() = ElementItemBinding::inflate
            // recyclerView 아이템 클릭 이벤트 설정
            override val setOnItemClickListener: OnItemClickListener get() = object : OnItemClickListener {
                override fun onItemClick(position: Int) {
                    // Bundle 을 이용해 position 에 해당 이미지 URL 넘기기
                    fragment.requireActivity().supportFragmentManager.commit {
                        replace(
                            R.id.container_view,
                            ItemDetailFragment.indexImage(dataList[position].iconDrawable)
                        )
                    }
                }
            }
            override fun onBindItem(binding: ElementItemBinding, item: MainItem) {
                bindMainItemToView(fragment.requireContext(), binding, item)
            }
        }
    }

    fun setupBottomSheetForMapView(
        fragment: Fragment,
        bottomSheetLayout: ConstraintLayout
    ): BaseBottomSheetLayout {
        val bottomSheet = object : BaseBottomSheetLayout(bottomSheetLayout) {
            override val height: Int get() = getMaxHeightBasedOnScreenHeight(fragment)
            // BottomSheetBehavior 의 state 를 접혀 있는 상태 설정
            override val state: Int get() = BottomSheetBehavior.STATE_COLLAPSED
            // BottomSheetBehavior 로 설정된 뷰가 파괴되고 다시 생성될 때 모든 속성 유지
            override val saveFlags: Int get() = BottomSheetBehavior.SAVE_ALL
        }
        bottomSheet.initializeSize()
        bottomSheet.initializeState()
        return bottomSheet
    }

    private fun getMaxHeightBasedOnScreenHeight(fragment: Fragment): Int {
        val displayMetrics = DisplayMetrics()
        fragment.requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels
        return (screenHeight * 0.50).toInt()
    }

    private fun Context.getColorCompat(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }

    private fun Context.getDrawableCompat(@DrawableRes drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(this, drawableRes)
    }
}