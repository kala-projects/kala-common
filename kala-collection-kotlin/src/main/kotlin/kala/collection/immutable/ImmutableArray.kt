@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kala.collection.immutable

typealias ImmutableArray<E> = asia.kala.collection.immutable.ImmutableArray<out E>

inline fun <E> immutableArrayOf(vararg elements: E): ImmutableArray<E> {
    return ImmutableArray.of(*elements)
}

inline fun <E> ImmutableArray<E>.updated(index: Int, newValue: E): ImmutableArray<E> {
    return (this as asia.kala.collection.immutable.ImmutableArray<E>).updated(index, newValue)
}

inline fun <E> ImmutableArray<E>.prepended(value: E): ImmutableArray<E> {
    return (this as asia.kala.collection.immutable.ImmutableArray<E>).prepended(value)
}

inline fun <E> ImmutableArray<E>.prependedAll(prefix: Iterable<E>): ImmutableArray<E> {
    return (this as asia.kala.collection.immutable.ImmutableArray<E>).prependedAll(prefix)
}

inline fun <E> ImmutableArray<E>.prependedAll(prefix: Array<out E>): ImmutableArray<E> {
    return (this as asia.kala.collection.immutable.ImmutableArray<E>).prependedAll(prefix)
}

inline fun <E> ImmutableArray<E>.appended(value: E): ImmutableArray<E> {
    return (this as asia.kala.collection.immutable.ImmutableArray<E>).appended(value)
}

inline fun <E> ImmutableArray<E>.appendedAll(postfix: Iterable<E>): ImmutableArray<E> {
    return (this as asia.kala.collection.immutable.ImmutableArray<E>).appendedAll(postfix)
}

inline fun <E> ImmutableArray<E>.appendedAll(postfix: Array<out E>): ImmutableArray<E> {
    return (this as asia.kala.collection.immutable.ImmutableArray<E>).appendedAll(postfix)
}

inline operator fun <E> ImmutableArray<E>.plus(element: E): ImmutableArray<E> {
    return appended(element)
}

inline operator fun <E> ImmutableArray<E>.plus(elements: Iterable<E>): ImmutableArray<E> {
    return appendedAll(elements)
}

inline operator fun <E> ImmutableArray<E>.plus(elements: Array<out E>): ImmutableArray<E> {
    return appendedAll(elements)
}