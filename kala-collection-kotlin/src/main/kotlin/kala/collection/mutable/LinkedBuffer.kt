@file:Suppress("NOTHING_TO_INLINE")

package kala.collection.mutable

typealias LinkedBuffer<E> = asia.kala.collection.mutable.LinkedBuffer<E>

inline fun <E> linkedBufferOf(vararg elements: E): LinkedBuffer<E> {
    return LinkedBuffer.from(elements)
}