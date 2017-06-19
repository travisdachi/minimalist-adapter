package com.github.travisp101.minimalistadapter.viewpager

import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import kotlin.properties.Delegates

abstract class MinimalPagerAdapter<T>(list: List<T> = emptyList()) : PagerAdapter() {

    open var list: List<T> by Delegates.observable(list) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun isViewFromObject(view: View?, any: Any?): Boolean = view === any

    override fun getCount(): Int = list.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return getMinimalView(container, position, list[position]).apply {
            container.addView(this)
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        container.removeView(any as View)
    }

    abstract fun getMinimalView(container: ViewGroup, position: Int, item: T): View

    companion object {
        inline fun <T> create(
                crossinline getView: (container: ViewGroup, position: Int, item: T) -> View
        ): MinimalPagerAdapter<T> = object : MinimalPagerAdapter<T>() {
            override fun getMinimalView(container: ViewGroup, position: Int, item: T): View {
                return getView(container, position, item)
            }
        }
    }
}