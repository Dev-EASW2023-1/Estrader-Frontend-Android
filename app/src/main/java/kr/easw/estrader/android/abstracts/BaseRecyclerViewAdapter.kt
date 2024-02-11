package kr.easw.estrader.android.abstracts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

// recyclerView 에 사용할 커스텀 리스너 interface 정의
interface OnItemClickListener {
    fun onItemClick(position: Int)
}

abstract class BaseRecyclerViewAdapter <T: Any, VB: ViewBinding> (
    private val dataSet: MutableList<T>
): RecyclerView.Adapter<BaseViewHolder<T>>(){

    abstract val setupItemBinding: Inflate<VB>
    abstract val setOnItemClickListener: OnItemClickListener
    abstract fun onBindItem(binding: VB, item: T)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<T> {
        val view = setupItemBinding.invoke(LayoutInflater.from(parent.context), parent, false)
        return object: BaseViewHolder<T>(view.root, setOnItemClickListener) {
            override fun bind(item: T) {
                this@BaseRecyclerViewAdapter.onBindItem(view, item)
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

    open fun setTitleSelected(position: Int) {}

    open fun triggerScrollEventOnPass() {}
}