package kr.easw.estrader.android.model.data

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kr.easw.estrader.android.application.GlideApp
import kr.easw.estrader.android.databinding.ElementItemBinding
import kr.easw.estrader.android.databinding.ElementRealtoritemBinding
import kr.easw.estrader.android.fragment.BaseFragment
import kr.easw.estrader.android.model.dto.DelegateCompletionItem
import kr.easw.estrader.android.model.dto.DelegateItem
import kr.easw.estrader.android.model.dto.MainItem

class MainHolder(
    private val binding: ElementItemBinding?,
    listener: BaseFragment.OnItemClickListener?
) : RecyclerView.ViewHolder(binding!!.root) {
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
        GlideApp.with(itemView)
            .load(item.iconDrawable)
            .override(100, 100)
            .into(binding!!.image)
        auctionHouse.text = item.auctionHouse
        caseNumber.text = item.caseNumber
        location.text = item.location
        reservePrice.text = item.reservePrice
        auctionPeriod.text = item.auctionPeriod
    }
}

class DelegateHolder(
    binding: ElementRealtoritemBinding?,
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
    binding: ElementRealtoritemBinding?,
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