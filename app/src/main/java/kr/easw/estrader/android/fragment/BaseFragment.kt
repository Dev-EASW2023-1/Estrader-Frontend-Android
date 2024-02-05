package kr.easw.estrader.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * Fragment 사용할 때 보일러 코드 최소화 하기 위한 추상 클래스
 */

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) : Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    // recyclerView 에 사용할 커스텀 리스너 interface 정의
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // View 객체 생성
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    // View 작업 수행
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    // 메모리 누수 방지용 binding 참조 해제
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}