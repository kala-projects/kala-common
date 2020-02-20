@file:Suppress("NOTHING_TO_INLINE")

package kala.collection.mutable

typealias MArray<E> = asia.kala.collection.mutable.MArray<E>

inline fun <E> mutableArrayOf(vararg elements: E): MArray<E> {
    return MArray.of(*elements)
}

inline fun <E> Array<E>.asKala(): MArray<E> {
    return MArray.wrap(this)
}