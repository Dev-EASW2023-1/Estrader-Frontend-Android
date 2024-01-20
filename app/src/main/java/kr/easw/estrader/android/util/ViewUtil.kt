package kr.easw.estrader.android.util

import android.util.DisplayMetrics
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kr.easw.estrader.android.R
import kr.easw.estrader.android.abstracts.BaseBottomSheetLayout
import kr.easw.estrader.android.abstracts.BasePagingAdapter
import kr.easw.estrader.android.abstracts.Inflate
import kr.easw.estrader.android.abstracts.OnItemClickListener
import kr.easw.estrader.android.databinding.ElementItemBinding
import kr.easw.estrader.android.fragment.user.ItemLookUpFragment
import kr.easw.estrader.android.model.data.bindMainItemToView
import kr.easw.estrader.android.model.dto.MainItem

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

    fun setupBottomSheetForMapView(
        fragment: Fragment,
        Button: AppCompatButton,
        bottomSheetLayout: ConstraintLayout
    ): BaseBottomSheetLayout {
        val bottomSheet = object : BaseBottomSheetLayout(Button, bottomSheetLayout) {
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
}