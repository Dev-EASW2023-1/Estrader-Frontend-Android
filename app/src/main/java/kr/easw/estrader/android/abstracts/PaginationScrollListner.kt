package kr.easw.estrader.android.abstracts

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationScrollListener(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    abstract var pageSize: Int

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        Log.d("X3 | onScrolled", "childCount 측정 $visibleItemCount")
        Log.d("X3 | onScrolled", "itemCount 측정 $totalItemCount")
        Log.d("X3 | onScrolled", "findFirstVisibleItemPosition 측정 $firstVisibleItemPosition")

        if (!isPaging() && !isLastPage()) {
            Log.d("X3 | onScrolled", "detectBottomScroll 작동")
            detectBottomScroll(visibleItemCount, totalItemCount, firstVisibleItemPosition)
        }
    }

    private fun detectBottomScroll(
        visibleItemCount: Int,
        totalItemCount: Int,
        firstVisibleItemPosition: Int
    ) {
        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
            && firstVisibleItemPosition >= 0
            && totalItemCount >= pageSize
        ) {
            Log.d("X3 | detectBottomScroll", "loadMoreItems 작동")
            loadMoreItems()
        }
    }

    abstract fun loadMoreItems()
    abstract fun isLastPage(): Boolean
    abstract fun isPaging(): Boolean
}
