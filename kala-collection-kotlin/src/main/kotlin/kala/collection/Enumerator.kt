@file:Suppress("NOTHING_TO_INLINE")

package kala.collection

typealias Enumerator<E> = asia.kala.collection.Enumerator<out E>

inline fun <E> Traversable<E>.enumerator(): Enumerator<E> {
    return this.iterator()
}

inline fun <E> Iterable<E>.enumerator(): Enumerator<E> {
    return Enumerator.fromJava(this.iterator())
}