package com.github.salonkasoli.moviesearchsample.core.rv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

/**
 * Вью холдер, которому можно передать лэйаут, а он сам его заинфлейтит.
 *
 * Есть полезная функция [BaseViewHolder.findView], позволяющая удобно искать вьюхи.
 */
open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    constructor(parent: ViewGroup, @LayoutRes layoutRes: Int) : this(
        LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
    )

    fun <T : View> findView(@IdRes id: Int) : T {
        return itemView.findViewById(id) as T
    }
}