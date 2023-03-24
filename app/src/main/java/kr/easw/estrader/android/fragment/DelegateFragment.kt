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

class DelegateFragment : Fragment() {
    private var _binding: FragmentDelegateBinding? = null
    private val binding get() = _binding!!
    private var dataList: MutableList<DelegateRecyclerViewItem>? = null
    private var itemClickListener: OnItemClickListener? = null
    private lateinit var recyclerViewBinding: ElementDelegateitemBinding

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
        initRecyclerView()
    }

    private fun initializeData() {
        dataList = mutableListOf(
            DelegateRecyclerViewItem(
                "남재경",
                "테스트1",
                "테스트1",
                "테스트1",
                "테스트1"
            ), DelegateRecyclerViewItem(
                "허석무",
                "테스트1",
                "테스트1",
                "테스트1",
                "테스트1"
            ),
            DelegateRecyclerViewItem(
                "김성준",
                "테스트1",
                "테스트1",
                "테스트1",
                "테스트1"
            ),
            DelegateRecyclerViewItem(
                "최이루",
                "테스트1",
                "테스트1",
                "테스트1",
                "테스트1"
            ),
            DelegateRecyclerViewItem(
                "임정수",
                "테스트1",
                "테스트1",
                "테스트1",
                "테스트1"
            ),
            DelegateRecyclerViewItem(
                "엄선용",
                "테스트1",
                "테스트1",
                "테스트1",
                "테스트1"
            )
        )
    }

    private fun initRecyclerView() {
        val recyclerViewAdapter = object : RecyclerView.Adapter<DelegateViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): DelegateViewHolder {
                recyclerViewBinding = ElementDelegateitemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                return DelegateViewHolder(recyclerViewBinding, itemClickListener)
            }

            override fun onBindViewHolder(
                holder: DelegateViewHolder, position: Int
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

private data class DelegateRecyclerViewItem(
    val username: String,
    val auctionHouse: String,
    val caseNumber: String,
    val location: String,
    val reservePrice: String
)

private class DelegateViewHolder(
    binding: ElementDelegateitemBinding, listener: DelegateFragment.OnItemClickListener?
) : RecyclerView.ViewHolder(binding.root) {
    val username: TextView = binding.delegateUsername
    val auctionHouse: TextView = binding.delegateAuctionhouse
    val caseNumber: TextView = binding.delegateCasenumber
    val location: TextView = binding.delegateLocation
    val reservePrice: TextView = binding.delegateReserveprice

    init {
        binding.root.setOnClickListener {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                listener?.onItemClick(pos)
            }
        }
    }

    fun bind(item: DelegateRecyclerViewItem) {
        username.text = item.username
        auctionHouse.text = item.auctionHouse
        caseNumber.text = item.caseNumber
        location.text = item.location
        reservePrice.text = item.reservePrice
    }
}