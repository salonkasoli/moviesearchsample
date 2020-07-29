package com.github.salonkasoli.moviesearchsample.core.rv

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Штука, отвечающая за пагинацию.
 * Дергает loadMoreListener, когда юзер доскролил до threshold элемента снизу.
 */
class PaginationController(
    list: RecyclerView,
    private val threshold: Int = 5
) {

    var listSizeProvider: (() -> Int)? = null

    var loadMoreListener: (() -> Unit)? = null

    var isEnabled: Boolean = true
    var isLoading: Boolean = false

    init {
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!isEnabled || isLoading || listSizeProvider == null) {
                    return
                }

                val layoutManager = list.layoutManager as LinearLayoutManager
                val lastPosition: Int = layoutManager.findLastVisibleItemPosition()
                if (lastPosition > listSizeProvider!!.invoke() - threshold) {
                    isLoading = true
                    loadMoreListener?.invoke()
                }
            }
        })
    }
}