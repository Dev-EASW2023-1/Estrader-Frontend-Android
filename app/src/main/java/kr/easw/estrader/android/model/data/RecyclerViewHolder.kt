package kr.easw.estrader.android.model.data

import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.easw.estrader.android.databinding.ElementDelegateitemBinding
import kr.easw.estrader.android.databinding.ElementItemlistBinding
import kr.easw.estrader.android.fragment.BaseFragment
import kr.easw.estrader.android.model.dto.DelegateCompletionItem
import kr.easw.estrader.android.model.dto.DelegateItem
import kr.easw.estrader.android.model.dto.MainItem

class MainHolder(
    binding: ElementItemlistBinding?,
    listener: BaseFragment.OnItemClickListener?
) : RecyclerView.ViewHolder(binding!!.root) {
    private val img: ImageView = binding!!.image
    private val auctionHouse: TextView = binding!!.auctionhouse
    private val caseNumber: TextView = binding!!.casenumber
    private val location: TextView = binding!!.location
    private val reservePrice: TextView = binding!!.reserveprice
    private val auctionPeriod: TextView = binding!!.auctionperiod

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

class DelegateHolder(
    binding: ElementDelegateitemBinding?,
    listener: BaseFragment.OnItemClickListener?
) : RecyclerView.ViewHolder(binding!!.root) {
    private val username: TextView = binding!!.username
    private val auctionHouse: TextView = binding!!.auctionhouse
    private val caseNumber: TextView = binding!!.casenumber
    private val location: TextView = binding!!.location
    private val reservePrice: TextView = binding!!.reserveprice
    private val auctionPeriod: TextView = binding!!.auctionperiod

    init {
        binding!!.root.setOnClickListener {
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

class DelegateCompletionHolder(
    binding: ElementDelegateitemBinding?,
    listener: BaseFragment.OnItemClickListener?
) : RecyclerView.ViewHolder(binding!!.root) {
    private val username: TextView = binding!!.username
    private val auctionHouse: TextView = binding!!.auctionhouse
    private val caseNumber: TextView = binding!!.casenumber
    private val location: TextView = binding!!.location
    private val reservePrice: TextView = binding!!.reserveprice
    private val auctionPeriod: TextView = binding!!.auctionperiod

    init {
        binding!!.root.setOnClickListener {
            val pos = adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                listener?.onItemClick(pos)
            }
        }
    }

    fun bind(item: DelegateCompletionItem) {
        username.text = item.username
        auctionHouse.text = item.auctionHouse
        caseNumber.text = item.caseNumber
        location.text = item.location
        reservePrice.text = item.reservePrice
        auctionPeriod.text = item.auctionPeriod
    }
}