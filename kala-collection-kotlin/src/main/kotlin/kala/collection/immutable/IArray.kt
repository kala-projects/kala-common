@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kala.collection.immutable

typealias IArray<E> = asia.kala.collection.immutable.IArray<out E>

inline fun <E> immutableArrayOf(vararg elements: E): IArray<E> {
    return IArray.of(*elements)
}

inline fun <E> IArray<E>.updated(index: Int, newValue: E): IArray<E> {
    return (this as asia.kala.collection.immutable.IArray<E>).updated(index, newValue)
}

inline fun <E> IArray<E>.prepended(value: E): IArray<E> {
    return (this as asia.kala.collection.immutable.IArray<E>).prepended(value)
}

inline fun <E> IArray<E>.prependedAll(prefix: Iterable<E>): IArray<E> {
    return (this as asia.kala.collection.immutable.IArray<E>).prependedAll(prefix)
}

inline fun <E> IArray<E>.prependedAll(prefix: Array<out E>): IArray<E> {
    return (this as asia.kala.collection.immutable.IArray<E>).prependedAll(prefix)
}

inline fun <E> IArray<E>.appended(value: E): IArray<E> {
    return (this as asia.kala.collection.immutable.IArray<E>).appended(value)
}

inline fun <E> IArray<E>.appendedAll(postfix: Iterable<E>): IArray<E> {
    return (this as asia.kala.collection.immutable.IArray<E>).appendedAll(postfix)
}

inline fun <E> IArray<E>.appendedAll(postfix: Array<out E>): IArray<E> {
    return (this as asia.kala.collection.immutable.IArray<E>).appendedAll(postfix)
}

inline operator fun <E> IArray<E>.plus(element: E): IArray<E> {
    return appended(element)
}

inline operator fun <E> IArray<E>.plus(elements: Iterable<E>): IArray<E> {
    return appendedAll(elements)
}

inline operator fun <E> IArray<E>.plus(elements: Array<out E>): IArray<E> {
    return appendedAll(elements)
}