package kr.easw.estrader.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.easw.estrader.android.databinding.ElementDelegateitemBinding
import kr.easw.estrader.android.databinding.FragmentDelegateBinding
import kr.easw.estrader.android.model.dto.DelegateItem

/**
 * 대리인 전용 메인 화면 Fragment
 * 대리 위임 신청 목록
 */
class DelegateFragment : Fragment() {
    private var _binding: FragmentDelegateBinding? = null
    private val binding get() = _binding!!
    private var dataList: MutableList<DelegateItem>? = null
    private var itemClickListener: OnItemClickListener? = null
    private lateinit var recyclerBinding: ElementDelegateitemBinding

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDelegateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        initializeData()
        initRecycler()
    }

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

    private fun initRecycler() {
        val recyclerViewAdapter = object : RecyclerView.Adapter<DelegateHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): DelegateHolder {
                recyclerBinding = ElementDelegateitemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return DelegateHolder(recyclerBinding, itemClickListener)
            }

            override fun onBindViewHolder(
                holder: DelegateHolder, position: Int
            ) {
                holder.bind(dataList!![position])
            }

            override fun getItemCount(): Int = dataList!!.size

            fun setOnItemClickListener(listener: OnItemClickListener) {
                itemClickListener = listener
            }
        }

        binding.delegateRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerViewAdapter
        }

        recyclerViewAdapter.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
//                requireActivity().supportFragmentManager.commitNow {
//                    replace(R.id.mainlist_framelayout, ItemLookUpFragment())
//                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private class DelegateHolder(
    binding: ElementDelegateitemBinding,
    listener: DelegateFragment.OnItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {
    val username: TextView = binding.username
    val auctionHouse: TextView = binding.auctionhouse
    val caseNumber: TextView = binding.casenumber
    val location: TextView = binding.location
    val reservePrice: TextView = binding.reserveprice
    val auctionPeriod: TextView = binding.auctionperiod

    init {
        binding.root.setOnClickListener {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                listener?.onItemClick(pos)
            }
        }
    }

    fun bind(item: DelegateItem) {
        username.text = item.username
        auctionHouse.text = item.auctionHouse
        caseNumber.text = item.caseNumber
        location.text = item.location
        reservePrice.text = item.reservePrice
        auctionPeriod.text = item.auctionPeriod
    }
}