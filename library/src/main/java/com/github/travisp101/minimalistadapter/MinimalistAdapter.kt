package com.github.travisp101.minimalistadapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

open class MinimalViewHolder<T : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var item: T by Delegates.notNull<T>()
}

typealias MinimalOnClickListener<VH> = (holder: VH, view: View) -> Unit

abstract class MinimalListAdapter<T : Any, VH : MinimalViewHolder<T>>(
        list: List<T> = emptyList(),
        open val minimalOnClickListener: MinimalOnClickListener<VH>? = null,
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
            minimalOnClickListener?.let { onClick ->
                itemView.setOnClickListener {
                    onClick(this, it)
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

    companion object {
        fun <T : Any, VH : MinimalViewHolder<T>> create(
                onCreate: (parent: ViewGroup, viewType: Int) -> VH,
                onBind: (holder: VH, position: Int, item: T) -> Unit,
                list: List<T> = emptyList(),
                minimalOnClickListener: MinimalOnClickListener<VH>? = null,
                diffCalculator: DiffCalculator<T>? = null
        ): MinimalListAdapter<T, VH> = object : MinimalListAdapter<T, VH>(list, minimalOnClickListener, diffCalculator) {
            override fun onCreateMinimalViewHolder(parent: ViewGroup, viewType: Int): VH = onCreate(parent, viewType)

            override fun onBindMinimalViewHolder(holder: VH, position: Int, item: T) = onBind(holder, position, item)
        }
    }
}