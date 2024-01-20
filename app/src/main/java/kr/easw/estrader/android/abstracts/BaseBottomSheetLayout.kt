package kr.easw.estrader.android.abstracts

import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

abstract class BaseBottomSheetLayout(
    Button: AppCompatButton,
    private val bottomSheetLayout: ConstraintLayout
) {
    abstract val height: Int
    abstract val state: Int
    abstract val saveFlags: Int

    // BottomSheetBehavior 에 layout 설정
    private val bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout> = BottomSheetBehavior.from(bottomSheetLayout)

    // 버튼 이벤트 설정
    init {
        Button.setOnClickListener {
            initializeBehavior()
        }
        bottomSheetLayout.setOnClickListener {
            initializeBehavior()
        }
    }

    // view 의 size 가 바뀌면 requestLayout() 사용
    fun initializeSize() {
        bottomSheetLayout.layoutParams.height = height
        bottomSheetLayout.requestLayout()
    }

    fun initializeState() {
        bottomSheetBehavior.state = state
        bottomSheetBehavior.saveFlags = saveFlags
    }

    private fun initializeBehavior() {
        bottomSheetBehavior.state =
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                BottomSheetBehavior.STATE_EXPANDED
            } else {
                BottomSheetBehavior.STATE_COLLAPSED
            }
    }
}