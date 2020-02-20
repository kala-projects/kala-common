@file:Suppress("NOTHING_TO_INLINE")

package kala.collection.mutable

typealias ArrayBuffer<E> = asia.kala.collection.mutable.ArrayBuffer<E>

inline fun <E> arrayBufferOf(vararg elements: E): ArrayBuffer<E> {
    return ArrayBuffer.of(*elements)
}