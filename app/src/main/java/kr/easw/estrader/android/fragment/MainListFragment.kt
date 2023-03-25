package kr.easw.estrader.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ElementItemlistBinding
import kr.easw.estrader.android.databinding.FragmentMainlistBinding
import kr.easw.estrader.android.model.dto.MainItem
import java.lang.ref.WeakReference

/**
 * 사용자 메인 화면 Fragment
 * 리스트 항목을 누르면 ItemLookUpFragment 로 이동
 */
class MainListFragment : Fragment() {
    private var _binding: FragmentMainlistBinding? = null
    private val binding get() = _binding!!
    private var dataList: MutableList<MainItem>? = null
    private var itemClickListener: WeakReference<OnItemClickListener>? = null
    private var recyclerBinding: ElementItemlistBinding? = null

    // recyclerView 에 사용할 커스텀 리스너 interface 정의
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // View 객체 생성
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    // View 작업 수행
    override fun onViewCreated(
        view: View, savedInstanceState: Bundle?
    ) {
        initializeData()
        initRecycler()
    }

    // 메모리 누수 방지용 binding 참조 해제
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        recyclerBinding = null
    }

    // ViewHolder 에 사용할 DateList 초기화
    private fun initializeData() {
        dataList = mutableListOf(
            MainItem(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_house1)!!,
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27 ~ \n04-07"
            ), MainItem(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_house2)!!,
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27 ~ \n04-07"
            ), MainItem(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_house3)!!,
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27 ~ \n04-07"
            ), MainItem(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_house4)!!,
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27 ~ \n04-07"
            ), MainItem(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_house1)!!,
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27 ~ \n04-07"
            ), MainItem(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_house2)!!,
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27 ~ \n04-07"
            ), MainItem(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_house3)!!,
                "대구지방법원",
                "2022타경112663",
                "대구광역시 중구",
                "1,489,129,980",
                "03-27 ~ \n04-07"
            ), MainItem(
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_house4)!!,
                "대구지방법원",
                "2022타경111158",
                "대구광역시 수성구",
                "438,000,000",
                "03-27 ~ \n04-07"
            )
        )
    }

    // recyclerView Adapter 단 한 번만 쓰기 때문에 익명 클래스 선언
    private fun initRecycler() {
        val recyclerAdapter = object : RecyclerView.Adapter<MainHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): MainHolder {
                recyclerBinding = ElementItemlistBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return MainHolder(recyclerBinding, itemClickListener?.get())
            }

            override fun onBindViewHolder(
                holder: MainHolder, position: Int
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

        binding.mainlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
        }

        // recyclerView 아이템 클릭 이벤트 설정
        recyclerAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                requireActivity().supportFragmentManager.commit {
                    replace(R.id.framelayout, ItemLookUpFragment())
                }
            }
        })
    }
}

// recyclerView 에 사용할 ViewHolder
private class MainHolder(
    binding: ElementItemlistBinding?,
    listener: MainListFragment.OnItemClickListener?
) : RecyclerView.ViewHolder(binding!!.root) {
    val img: ImageView = binding!!.image
    val auctionHouse: TextView = binding!!.auctionhouse
    val caseNumber: TextView = binding!!.casenumber
    val location: TextView = binding!!.location
    val reservePrice: TextView = binding!!.reserveprice
    val auctionPeriod: TextView = binding!!.auctionperiod

    init {
        binding!!.root.setOnClickListener {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                listener?.onItemClick(pos)
            }
        }
    }

    fun bind(item: MainItem) {
        img.setImageDrawable(item.iconDrawable)
        auctionHouse.text = item.auctionHouse
        caseNumber.text = item.caseNumber
        location.text = item.location
        reservePrice.text = item.reservePrice
        auctionPeriod.text = item.auctionPeriod
    }
}