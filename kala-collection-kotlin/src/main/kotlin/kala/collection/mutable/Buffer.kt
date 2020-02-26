@file:Suppress("NOTHING_TO_INLINE")

package kala.collection.mutable

typealias Buffer<E> = asia.kala.collection.mutable.Buffer<E>

inline operator fun <E> Buffer<E>.plusAssign(value: E) {
    this.append(value)
}

inline operator fun <E> Buffer<E>.plusAssign(values: Iterable<E>) {
    this.appendAll(values)
}

inline operator fun <E> Buffer<E>.plusAssign(values: Array<out E>) {
    this.appendAll(values)
}

inline fun <E> MutableList<E>.asKalaBuffer(): Buffer<E> {
    return Buffer.wrapJava(this)
}

