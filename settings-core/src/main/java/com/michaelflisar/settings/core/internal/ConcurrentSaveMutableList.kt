package com.michaelflisar.settings.core.internal

/*
 * this class provided a concurrent save forEach method
 */
internal class ConcurrentSaveMutableList<E>() {

    private val list = ArrayList<E>()
    private var isIterating = false

    val size: Int
        get() = list.size
    val sizePending: Int
        get() = pendingAdditions.size + pendingDeletes.size

    private val pendingAdditions = ArrayList<E>()
    private val pendingDeletes = ArrayList<E>()

    fun clear() {
        pendingAdditions.clear()
        pendingDeletes.clear()
        list.clear()
    }

    fun add(item: E) {
        if (isIterating) {
            pendingAdditions.add(item)
        } else {
            list.add(item)
        }
    }

    fun remove(item: E) {
        if (isIterating) {
            pendingDeletes.add(item)
        } else {
            list.remove(item)
        }
    }

    fun forEach(callback: (E) -> Unit) {
        isIterating = true
        list.forEach { callback(it) }
        isIterating = false
        runPending()
    }

    private fun runPending() {
        pendingDeletes.forEach {
            list.remove(it)
        }
        pendingAdditions.forEach {
            list.add(it)
        }
        pendingDeletes.clear()
        pendingAdditions.clear()
    }

}