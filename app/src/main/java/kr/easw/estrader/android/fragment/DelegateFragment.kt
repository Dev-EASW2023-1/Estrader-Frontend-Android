package kr.easw.estrader.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.easw.estrader.android.databinding.ElementDelegateitemBinding
import kr.easw.estrader.android.databinding.FragmentDelegateBinding
import kr.easw.estrader.android.model.data.DelegateHolder
import kr.easw.estrader.android.model.dto.DelegateItem
import java.lang.ref.WeakReference

/**
 * 대리인 전용 메인화면 Fragment
 * 대리위임 신청 목록
 */
class DelegateFragment : BaseFragment<FragmentDelegateBinding>(FragmentDelegateBinding::inflate) {

    private var dataList: MutableList<DelegateItem>? = null
    private var itemClickListener: WeakReference<OnItemClickListener>? = null
    private var recyclerBinding: ElementDelegateitemBinding? = null

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        initializeData()
        initRecycler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerBinding = null
    }

    // ViewHolder 에 사용할 DateList 초기화
    private fun initializeData() {
        dataList = mutableListOf(
            DelegateItem(
                "남재경",
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27 ~ \n04-07"
            ), DelegateItem(
                "허석무",
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27 ~ \n04-07"
            ),
            DelegateItem(
                "김성준",
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27 ~ \n04-07"
            ),
            DelegateItem(
                "최이루",
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27 ~ \n04-07"
            ),
            DelegateItem(
                "임정수",
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27 ~ \n04-07"
            ),
            DelegateItem(
                "엄선용",
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27 ~ \n04-07"
            )
        )
    }

    override fun initRecycler() {
        val recyclerViewAdapter = object : RecyclerView.Adapter<DelegateHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): DelegateHolder {
                recyclerBinding = ElementDelegateitemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return DelegateHolder(recyclerBinding, itemClickListener?.get())
            }

            override fun onBindViewHolder(
                holder: DelegateHolder, position: Int
            ) {
                holder.bind(dataList!![position])
            }

            override fun getItemCount(): Int = dataList!!.size

            // 리스너 객체 참조를 recyclerView Adapter 에 전달
            // 약한 참조를 위해 WeakReference 설정
            fun setOnItemClickListener(listener: OnItemClickListener) {
                itemClickListener = WeakReference(listener)
            }
        }

        (binding as FragmentDelegateBinding).delegateRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerViewAdapter
        }

        // recyclerView 아이템 클릭 이벤트 설정
        recyclerViewAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                // TODO("아직 정하지 않았음.")
            }
        })
    }
}