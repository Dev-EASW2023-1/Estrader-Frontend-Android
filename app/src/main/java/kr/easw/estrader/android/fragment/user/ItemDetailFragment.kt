package kr.easw.estrader.android.fragment.user

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kr.easw.estrader.android.abstracts.BaseRecyclerViewAdapter
import kr.easw.estrader.android.databinding.ElementTabItemBinding
import kr.easw.estrader.android.databinding.FragmentItemDetailBinding
import kr.easw.estrader.android.fragment.BaseFragment
import kr.easw.estrader.android.model.dto.TitleItem
import kr.easw.estrader.android.util.ViewUtil

class ItemDetailFragment : BaseFragment<FragmentItemDetailBinding>(FragmentItemDetailBinding::inflate) {
    private lateinit var itemTitleAdapter: BaseRecyclerViewAdapter<TitleItem, ElementTabItemBinding>
    private val dataList: MutableList<TitleItem> = mutableListOf()

    companion object {
        const val ARG_POSITION = "position"
        fun indexImage(iconDrawable: String) = ItemDetailFragment().apply {
            arguments = bundleOf(ARG_POSITION to iconDrawable)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setImageViewWithGlide()
        initRecycler()
    }

    private fun setImageViewWithGlide() {
        Glide.with(this)
            .load(arguments?.getString(ARG_POSITION).toString())
            .into(binding.mainimage)
    }

    private fun initRecycler() {
        dataList.apply {
            add(TitleItem("기본", true))
            add(TitleItem("사진", false))
        }
        itemTitleAdapter = ViewUtil.setupDetailItemTabAdapter(this, dataList, binding)
        binding.recyclerTab.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = itemTitleAdapter
        }
        itemTitleAdapter.triggerScrollEventOnPass()
    }
}