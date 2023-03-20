package kr.easw.estrader.android.fragment

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.easw.estrader.android.R
import kr.easw.estrader.android.databinding.ElementItemlistBinding
import kr.easw.estrader.android.databinding.FragmentMainlistBinding


class MainListFragment : Fragment() {
    private var _binding: FragmentMainlistBinding? = null
    private val binding get() = _binding!!
    private var dataList: MutableList<RecyclerViewItem>? = null
    private var itemClickListener: OnItemClickListener? = null
    private lateinit var recyclerViewBinding: ElementItemlistBinding

    interface OnItemClickListener{
        fun onItemClick(v: View, position: Int)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        dataList = mutableListOf(
            RecyclerViewItem(ContextCompat.getDrawable(requireContext(), R.drawable.ic_house1)!!,"테스트1","테스트1","테스트1","테스트1"),
            RecyclerViewItem(ContextCompat.getDrawable(requireContext(), R.drawable.ic_house2)!!,"테스트1","테스트1","테스트1","테스트1"),
            RecyclerViewItem(ContextCompat.getDrawable(requireContext(), R.drawable.ic_house3)!!,"테스트1","테스트1","테스트1","테스트1"),
            RecyclerViewItem(ContextCompat.getDrawable(requireContext(), R.drawable.ic_house4)!!,"테스트1","테스트1","테스트1","테스트1"),
            RecyclerViewItem(ContextCompat.getDrawable(requireContext(), R.drawable.ic_house1)!!,"테스트1","테스트1","테스트1","테스트1"),
            RecyclerViewItem(ContextCompat.getDrawable(requireContext(), R.drawable.ic_house2)!!,"테스트1","테스트1","테스트1","테스트1"),
            RecyclerViewItem(ContextCompat.getDrawable(requireContext(), R.drawable.ic_house3)!!,"테스트1","테스트1","테스트1","테스트1"),
            RecyclerViewItem(ContextCompat.getDrawable(requireContext(), R.drawable.ic_house4)!!,"테스트1","테스트1","테스트1","테스트1")
        )

        val recyclerViewAdapter = object: RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ViewHolder {
                recyclerViewBinding = ElementItemlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(recyclerViewBinding, requireContext())
            }

            override fun onBindViewHolder(
                holder: ViewHolder,
                position: Int
            ) {
                val item = dataList!![position]
                holder.bind(item)
            }

            override fun getItemCount(): Int = dataList!!.size

            fun setOnItemClickListener(listener: OnItemClickListener){
                itemClickListener = listener
            }
        }

        _binding!!.mainlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL,false)
            adapter = recyclerViewAdapter
        }

        recyclerViewAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(v: View, position: Int) {
                requireActivity().supportFragmentManager.commitNow {
                    replace(R.id.mainlist_framelayout, ItemLookUpFragment())
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private data class RecyclerViewItem (
    val iconDrawable: Drawable,
    val auctionHouse: String,
    val caseNumber: String,
    val location: String,
    val reservePrice: String
)

private class ViewHolder(binding: ElementItemlistBinding, context: Context)
    : RecyclerView.ViewHolder(binding.root) {
    var img: ImageView = binding.itemlistImage
    var auctionHouse: TextView = binding.itemlistAuctionhouse
    var caseNumber: TextView = binding.itemlistCasenumber
    var location: TextView = binding.itemlistLocation
    var reservePrice: TextView = binding.itemlistReserveprice

    init {
        binding.root.setOnClickListener {
//            if (adapterPosition != RecyclerView.NO_POSITION) {
//            }
        }
    }

    fun bind(item: RecyclerViewItem) {
        img.setImageDrawable(item.iconDrawable)
        auctionHouse.text = item.auctionHouse
        caseNumber.text = item.caseNumber
        location.text = item.location
        reservePrice.text = item.reservePrice
    }
}