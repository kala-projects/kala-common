@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kala.collection.immutable

typealias ImmutableVector<E> = asia.kala.collection.immutable.ImmutableVector<out E>

inline fun <E> immutableVectorOf(vararg elements: E): ImmutableVector<E> {
    return ImmutableVector.from(elements)
}

inline fun <E> ImmutableVector<E>.updated(index: Int, newValue: E): ImmutableVector<E> {
    return (this as asia.kala.collection.immutable.ImmutableVector<E>).updated(index, newValue)
}

inline fun <E> ImmutableVector<E>.prepended(value: E): ImmutableVector<E> {
    return (this as asia.kala.collection.immutable.ImmutableVector<E>).prepended(value)
}

inline fun <E> ImmutableVector<E>.prependedAll(prefix: Iterable<E>): ImmutableVector<E> {
    return (this as asia.kala.collection.immutable.ImmutableVector<E>).prependedAll(prefix)
}

inline fun <E> ImmutableVector<E>.prependedAll(prefix: Array<out E>): ImmutableVector<E> {
    return (this as asia.kala.collection.immutable.ImmutableVector<E>).prependedAll(prefix)
}

inline fun <E> ImmutableVector<E>.appended(value: E): ImmutableVector<E> {
    return (this as asia.kala.collection.immutable.ImmutableVector<E>).appended(value)
}

inline fun <E> ImmutableVector<E>.appendedAll(postfix: Iterable<E>): ImmutableVector<E> {
    return (this as asia.kala.collection.immutable.ImmutableVector<E>).appendedAll(postfix)
}

inline fun <E> ImmutableVector<E>.appendedAll(postfix: Array<out E>): ImmutableVector<E> {
    return (this as asia.kala.collection.immutable.ImmutableVector<E>).appendedAll(postfix)
}

inline operator fun <E> ImmutableVector<E>.plus(element: E): ImmutableVector<E> {
    return appended(element)
}

inline operator fun <E> ImmutableVector<E>.plus(elements: Iterable<E>): ImmutableVector<E> {
    return appendedAll(elements)
}

inline operator fun <E> ImmutableVector<E>.plus(elements: Array<out E>): ImmutableVector<E> {
    return appendedAll(elements)
}