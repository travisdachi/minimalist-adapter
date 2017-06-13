package com.github.travisp101.minimalistadapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

open class MinimalViewHolder<T : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var item: T by Delegates.notNull<T>()
}

abstract class MinimalListAdapter<T : Any, VH : MinimalViewHolder<T>>(
        list: List<T> = emptyList(),
        open val onItemClick: ((T) -> Unit)? = null,
        open val diffCalculator: DiffCalculator<T>? = null
) : RecyclerView.Adapter<VH>() {

    open var list: List<T> by Delegates.observable(list) { _, oldValue, newValue ->
        val diffResult = diffCalculator?.calculateDiff(oldValue, newValue)
        if (diffResult != null) diffResult.dispatchUpdatesTo(this)
        else notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return onCreateMinimalViewHolder(parent, viewType).apply {
            onItemClick?.let { onClick ->
                itemView.setOnClickListener {
                    onClick.invoke(item)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        list[position].let {
            holder.item = it
            onBindMinimalViewHolder(holder, position, it)
        }
    }

    abstract fun onCreateMinimalViewHolder(parent: ViewGroup, viewType: Int): VH

    abstract fun onBindMinimalViewHolder(holder: VH, position: Int, item: T)

}