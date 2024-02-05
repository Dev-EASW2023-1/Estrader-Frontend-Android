package kr.easw.estrader.android.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.easw.estrader.android.abstracts.BasePagingAdapter
import kr.easw.estrader.android.databinding.ElementItemBinding
import kr.easw.estrader.android.databinding.FragmentSearchNewBinding
import kr.easw.estrader.android.definitions.ApiDefinition
import kr.easw.estrader.android.definitions.PREFERENCE_TOKEN
import kr.easw.estrader.android.model.dto.ItemPageRequestDTO
import kr.easw.estrader.android.model.dto.MainItem
import kr.easw.estrader.android.util.PreferenceUtil
import kr.easw.estrader.android.util.ViewUtil

class SearchnewFragment : Fragment() {
    private var _binding: FragmentSearchNewBinding? = null
    private val binding get() = _binding!!
    private lateinit var pagingAdapter: BasePagingAdapter<MainItem, ElementItemBinding>

    private val dataList: MutableList<MainItem> = mutableListOf()
    private var currentPage: Int = 0
    private var size: Int = 5

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchNewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
        prepareListItem()
    }

    private fun initRecycler() {
        pagingAdapter = ViewUtil.setupAdapterForMapView(this, dataList)
        binding.newRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
            adapter = pagingAdapter
        }
    }

    private fun prepareListItem() {
        ApiDefinition.GET_ITEM_LIST.setRequestParams(
            ItemPageRequestDTO("상당구", currentPage, size)
        ).setListener { response ->
            response.itemDto.takeIf { it.isNotEmpty() }?.let {
                it.forEach { item ->
                    val newItem = MainItem(
                        item.photo,
                        item.court,
                        item.caseNumber,
                        item.location,
                        item.minimumBidPrice,
                        item.biddingPeriod,
                        item.xcoordinate,
                        item.ycoordinate,
                        item.district
                    )
                    dataList.add(
                        newItem
                    )
                }
                pagingAdapter.notifyItemChanged(0)

            } ?: run {
                pagingAdapter.removeLoadingView()
            }

        }
            .setRequestHeaders(
                mutableMapOf(
                    "Authorization" to "Bearer " + PreferenceUtil(requireContext()).init()
                        .start().getString(PREFERENCE_TOKEN)!!
                )
            )
            .build(requireContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}