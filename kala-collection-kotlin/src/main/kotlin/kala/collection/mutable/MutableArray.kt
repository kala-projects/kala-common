@file:Suppress("NOTHING_TO_INLINE")

package kala.collection.mutable

typealias MutableArray<E> = asia.kala.collection.mutable.MutableArray<E>

inline fun <E> mutableArrayOf(vararg elements: E): MutableArray<E> {
    return MutableArray.of(*elements)
}

inline fun <E> Array<E>.asKala(): MutableArray<E> {
    return MutableArray.wrap(this)
}
