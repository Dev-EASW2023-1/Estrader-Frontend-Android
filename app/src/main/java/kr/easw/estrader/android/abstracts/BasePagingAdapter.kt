package kr.easw.estrader.android.abstracts

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kr.easw.estrader.android.databinding.ElementProgressBarBinding

abstract class BasePagingAdapter<T: Any, IB: ViewBinding> (
    private val dataSet: MutableList<T>
): RecyclerView.Adapter<BaseViewHolder<T>>(){
    companion object {
        const val VIEW_TYPE_ITEM = 0
        const val VIEW_TYPE_LOADING = 1
    }
    private val setupLoadingBinding: Inflate<ElementProgressBarBinding> = ElementProgressBarBinding::inflate
    private var position = 0

    abstract val setupItemBinding: Inflate<IB>
    abstract val setOnItemClickListener: OnItemClickListener
    abstract fun onBindItem(binding: IB, item: T)

    private var isLoaderVisible: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                Log.d("x3 | BasePagingAdapter()", "TYPE_ITEM 추가")
                val recyclerBinding = setupItemBinding.invoke(LayoutInflater.from(parent.context), parent, false)
                object: BaseViewHolder<T>(recyclerBinding.root, setOnItemClickListener) {
                    override fun bind(item: T) {
                        this@BasePagingAdapter.onBindItem(recyclerBinding, item)
                    }
                }
            }
            else -> {
                Log.d("x3 | BasePagingAdapter()", "TYPE_LOADING 추가")
                val loadingViewBinding = setupLoadingBinding.invoke(LayoutInflater.from(parent.context), parent, false)
                object: BaseViewHolder<T>(loadingViewBinding.root) {}
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        Log.d("x3 | BasePagingAdapter()", "onBindViewHolder 실행")
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == dataSet.lastIndex) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
        } else {
            VIEW_TYPE_ITEM
        }
    }

    // progress bar 추가
    fun addLoadingView(item: T) {
        Log.d("x3 | BasePagingAdapter()", "addLoadingView 실행")
        isLoaderVisible = true
        add(item)
    }

    // 로딩이 완료 후 Progress bar 삭제
    fun removeLoadingView() {
        Log.d("x3 | BasePagingAdapter()", "removeLoadingView 실행")
        isLoaderVisible = false
        position = dataSet.lastIndex
        dataSet.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clear() {
        while (itemCount > 0) remove(dataSet[0])
    }

    private fun add(item: T) {
        dataSet.add(item)
        position = dataSet.lastIndex
        notifyItemInserted(position)
    }

    private fun remove(item: T) {
        val position = dataSet.indexOf(item)

        if (position > -1) {
            dataSet.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}