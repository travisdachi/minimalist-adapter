package com.github.travisp101.minimalistadapter.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

open class MinimalViewHolder<T : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var item: T by Delegates.notNull<T>()
}

typealias MinimalOnClickListener<VH> = (view: View, holder: VH) -> Unit

abstract class MinimalListAdapter<T : Any, VH : MinimalViewHolder<T>>(
        list: List<T> = emptyList()
) : RecyclerView.Adapter<VH>() {

    open var list: List<T> by Delegates.observable(list) { _, oldValue, newValue ->
        val diffResult = diffCalculator?.calculateDiff(oldValue, newValue)
        if (diffResult != null) diffResult.dispatchUpdatesTo(this)
        else notifyDataSetChanged()
    }

    open val minimalOnClickListener: MinimalOnClickListener<VH>? = null
    open val diffCalculator: DiffCalculator<T>? = null

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vh = onCreateMinimalViewHolder(parent, viewType)
        val onClick = minimalOnClickListener
        if (onClick != null) vh.itemView.setOnClickListener { onClick.invoke(it, vh) }
        return vh
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.item = item
        onBindMinimalViewHolder(holder, position, item)
    }

    abstract fun onCreateMinimalViewHolder(parent: ViewGroup, viewType: Int): VH

    abstract fun onBindMinimalViewHolder(holder: VH, position: Int, item: T)

    companion object {
        inline fun <T : Any, VH : MinimalViewHolder<T>> create(
                crossinline onCreate: (parent: ViewGroup, viewType: Int) -> VH,
                crossinline onBind: (holder: VH, position: Int, item: T) -> Unit,
                list: List<T> = emptyList(),
                crossinline minimalOnClickListener: MinimalOnClickListener<VH>? = null,
                diffCalculator: DiffCalculator<T>? = SimpleDiffCalculator()
        ): MinimalListAdapter<T, VH> = object : MinimalListAdapter<T, VH>(list) {

            override val minimalOnClickListener: MinimalOnClickListener<VH>? = minimalOnClickListener

            override val diffCalculator: DiffCalculator<T>? = diffCalculator

            override fun onCreateMinimalViewHolder(parent: ViewGroup, viewType: Int): VH = onCreate(parent, viewType)

            override fun onBindMinimalViewHolder(holder: VH, position: Int, item: T) = onBind(holder, position, item)
        }
    }
}