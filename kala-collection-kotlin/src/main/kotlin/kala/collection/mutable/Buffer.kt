@file:Suppress("NOTHING_TO_INLINE")

package kala.collection.mutable

typealias Buffer<E> = asia.kala.collection.mutable.Buffer<E>

inline operator fun <E> Buffer<E>.plusAssign(value: E) {
    this.append(value)
}

