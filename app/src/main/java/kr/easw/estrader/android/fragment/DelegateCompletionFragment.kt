package kr.easw.estrader.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.easw.estrader.android.databinding.ElementDelegateitemBinding
import kr.easw.estrader.android.databinding.FragmentDelegatecompletionBinding
import kr.easw.estrader.android.model.data.DelegateCompletionHolder
import kr.easw.estrader.android.model.dto.DelegateCompletionItem
import java.lang.ref.WeakReference

/**
 * 대리인 전용 메인화면 Fragment
 * 대리위임 완료 목록
 */
class DelegateCompletionFragment : BaseFragment<FragmentDelegatecompletionBinding>(FragmentDelegatecompletionBinding::inflate){

    private var dataList: MutableList<DelegateCompletionItem>? = null
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
            DelegateCompletionItem(
                "남재경",
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27\n ~ \n04-07"
            ), DelegateCompletionItem(
                "김성준",
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27\n ~ \n04-07"
            ),
            DelegateCompletionItem(
                "엄선용",
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27\n ~ \n04-07"
            ),
            DelegateCompletionItem(
                "허석무",
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27\n ~ \n04-07"
            ),
            DelegateCompletionItem(
                "최이루",
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27\n ~ \n04-07"
            ),
            DelegateCompletionItem(
                "임정수",
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27\n ~ \n04-07"
            )
        )
    }

    override fun initRecycler() {
        val recyclerViewAdapter = object : RecyclerView.Adapter<DelegateCompletionHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): DelegateCompletionHolder {
                recyclerBinding = ElementDelegateitemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return DelegateCompletionHolder(recyclerBinding, itemClickListener?.get())
            }

            override fun onBindViewHolder(
                holder: DelegateCompletionHolder, position: Int
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

        (binding as FragmentDelegatecompletionBinding).delegatecompletionRecyclerView.apply {
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