@file:Suppress("UNCHECKED_CAST")

package kala.collection

typealias TraversableOnce<E> = asia.kala.collection.TraversableOnce<out E>
typealias Traversable<E> = asia.kala.collection.Traversable<out E>

inline fun <reified E> TraversableOnce<E>.toTypedArray(): Array<E> {
    return this.toArray { size: Int -> arrayOfNulls<E>(size) as Array<E> }
}