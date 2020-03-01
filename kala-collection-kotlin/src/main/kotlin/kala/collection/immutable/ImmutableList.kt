@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kala.collection.immutable

typealias ImmutableList<E> = asia.kala.collection.immutable.ImmutableList<out E>

inline fun <E> immutableLinkedListOf(): ImmutableList<E> {
    return ImmutableList.of()
}

inline fun <E> immutableLinkedListOf(vararg elements: E): ImmutableList<E> {
    return ImmutableList.from(elements)
}

inline fun <E> ImmutableList<E>.cons(value: E): ImmutableList<E> {
    return (this as asia.kala.collection.immutable.ImmutableList<E>).cons(value)
}

inline fun <E> ImmutableList<E>.updated(index: Int, newValue: E): ImmutableList<E> {
    return (this as asia.kala.collection.immutable.ImmutableList<E>).updated(index, newValue)
}

inline fun <E> ImmutableList<E>.prepended(value: E): ImmutableList<E> {
    return (this as asia.kala.collection.immutable.ImmutableList<E>).prepended(value)
}

inline fun <E> ImmutableList<E>.prependedAll(prefix: Iterable<E>): ImmutableList<E> {
    return (this as asia.kala.collection.immutable.ImmutableList<E>).prependedAll(prefix)
}

inline fun <E> ImmutableList<E>.prependedAll(prefix: Array<out E>): ImmutableList<E> {
    return (this as asia.kala.collection.immutable.ImmutableList<E>).prependedAll(prefix)
}

inline fun <E> ImmutableList<E>.appended(value: E): ImmutableList<E> {
    return (this as asia.kala.collection.immutable.ImmutableList<E>).appended(value)
}

inline fun <E> ImmutableList<E>.appendedAll(postfix: Iterable<E>): ImmutableList<E> {
    return (this as asia.kala.collection.immutable.ImmutableList<E>).appendedAll(postfix)
}

inline fun <E> ImmutableList<E>.appendedAll(postfix: Array<out E>): ImmutableList<E> {
    return (this as asia.kala.collection.immutable.ImmutableList<E>).appendedAll(postfix)
}

inline operator fun <E> ImmutableList<E>.plus(value: E): ImmutableList<E> {
    return appended(value)
}

inline operator fun <E> ImmutableList<E>.plus(postfix: Iterable<E>): ImmutableList<E> {
    return appendedAll(postfix)
}

inline operator fun <E> ImmutableList<E>.plus(postfix: Array<out E>): ImmutableList<E> {
    return appendedAll(postfix)
}