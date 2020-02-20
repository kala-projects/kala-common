@file:Suppress("NOTHING_TO_INLINE")

package kala.collection.immutable

typealias IArray<E> = asia.kala.collection.immutable.IArray<out E>

inline fun <E> immutableArrayOf(vararg elements: E): IArray<E> {
    return IArray.of(*elements)
}