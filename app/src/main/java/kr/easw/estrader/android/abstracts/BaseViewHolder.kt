package kr.easw.estrader.android.abstracts

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder <T: Any>(
    view: View,
    listener: OnItemClickListener? = null
): RecyclerView.ViewHolder(view) {

    init {
        if(listener != null) {
            view.setOnClickListener {
                val pos = absoluteAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(pos)
                }
            }
        }
    }

    open fun bind(item: T) {}
}