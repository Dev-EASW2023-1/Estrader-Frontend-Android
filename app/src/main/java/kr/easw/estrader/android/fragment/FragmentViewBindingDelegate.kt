package kr.easw.estrader.android.fragment

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// kotlin.properties.ReadOnlyProperty, kotlin.properties.ReadWriteProperty 두 개를 각각 상속 받아 property 활용 가능
// ReadWriteProperty<Fragment, T>를 사용할 수 있지만 View binding 은 변경할 수 없어서 읽기 전용 ReadOnlyProperty<Fragment, T> 확장
fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) = FragmentViewBindingDelegate(this, viewBindingFactory)
fun <T : ViewBinding> BottomSheetDialogFragment.viewBinding(viewBindingFactory: (View) -> T) = FragmentViewBindingDelegate(this, viewBindingFactory)

class FragmentViewBindingDelegate<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null

    init {
        // Lifecycle 획득
        // addObserver 를 사용 후 Owner 에 Observer 추가
        // fragment 의 lifecycle 을 구독 후, fragment 가 onDestroy 할 때 메모리 누수가 없도록 binding null 처리
        fragment.lifecycle.addObserver(object: DefaultLifecycleObserver {
            // DefaultLifecycleObserver 는 Lifecycle 상태 변경을 관찰할 수 있는 콜백 interface
            override fun onCreate(owner: LifecycleOwner) {
                // view 다시 생성할 때 새로운 LifecycleOwner 만드는 viewLifecycleOwnerLiveData 를 사용
                // ViewLifecycleOwner 는 onCreate 나 constructor 에서 사용 시 IllegalStateException 발생
                // 그러나 viewLifecycleOwnerLiveData 사용 시 ViewLifecycle 사용 시점을 신경 쓸 필요 없다.
                fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                    viewLifecycleOwner.lifecycle.addObserver(object: DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            binding = null
                        }
                    })
                }
            }
        })
    }

    // getValue 는 binding 객체의 instance 반환
    // binding 한 번만 초기화, 그 후 동일한 instance 재활용
    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val binding = binding
        if (binding != null) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
        }

        return viewBindingFactory(thisRef.requireView()).also { this.binding = it }
    }
}