@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kala.collection.immutable

typealias IVector<E> = asia.kala.collection.immutable.IVector<out E>

inline fun <E> immutableVectorOf(vararg elements: E): IVector<E> {
    return IVector.from(elements)
}

inline fun <E> IVector<E>.updated(index: Int, newValue: E): IVector<E> {
    return (this as asia.kala.collection.immutable.IVector<E>).updated(index, newValue)
}

inline fun <E> IVector<E>.prepended(value: E): IVector<E> {
    return (this as asia.kala.collection.immutable.IVector<E>).prepended(value)
}

inline fun <E> IVector<E>.prependedAll(prefix: Iterable<E>): IVector<E> {
    return (this as asia.kala.collection.immutable.IVector<E>).prependedAll(prefix)
}

inline fun <E> IVector<E>.prependedAll(prefix: Array<out E>): IVector<E> {
    return (this as asia.kala.collection.immutable.IVector<E>).prependedAll(prefix)
}

inline fun <E> IVector<E>.appended(value: E): IVector<E> {
    return (this as asia.kala.collection.immutable.IVector<E>).appended(value)
}

inline fun <E> IVector<E>.appendedAll(postfix: Iterable<E>): IVector<E> {
    return (this as asia.kala.collection.immutable.IVector<E>).appendedAll(postfix)
}

inline fun <E> IVector<E>.appendedAll(postfix: Array<out E>): IVector<E> {
    return (this as asia.kala.collection.immutable.IVector<E>).appendedAll(postfix)
}

inline operator fun <E> IVector<E>.plus(element: E): IVector<E> {
    return appended(element)
}

inline operator fun <E> IVector<E>.plus(elements: Iterable<E>): IVector<E> {
    return appendedAll(elements)
}

inline operator fun <E> IVector<E>.plus(elements: Array<out E>): IVector<E> {
    return appendedAll(elements)
}