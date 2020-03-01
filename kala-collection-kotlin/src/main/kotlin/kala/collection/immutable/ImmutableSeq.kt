@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package kala.collection.immutable

typealias ImmutableSeq<E> = asia.kala.collection.immutable.ImmutableSeq<out E>

inline fun <E> immutableSeqOf(vararg elements: E): ImmutableSeq<E> {
    return ImmutableSeq.of(*elements)
}

inline fun <E> ImmutableSeq<E>.updated(index: Int, newValue: E): ImmutableSeq<E> {
    return (this as asia.kala.collection.immutable.ImmutableSeq<E>).updated(index, newValue)
}

inline fun <E> ImmutableSeq<E>.prepended(value: E): ImmutableSeq<E> {
    return (this as asia.kala.collection.immutable.ImmutableSeq<E>).prepended(value)
}

inline fun <E> ImmutableSeq<E>.prependedAll(prefix: Iterable<E>): ImmutableSeq<E> {
    return (this as asia.kala.collection.immutable.ImmutableSeq<E>).prependedAll(prefix)
}

inline fun <E> ImmutableSeq<E>.prependedAll(prefix: Array<out E>): ImmutableSeq<E> {
    return (this as asia.kala.collection.immutable.ImmutableSeq<E>).prependedAll(prefix)
}

inline fun <E> ImmutableSeq<E>.appended(value: E): ImmutableSeq<E> {
    return (this as asia.kala.collection.immutable.ImmutableSeq<E>).appended(value)
}

inline fun <E> ImmutableSeq<E>.appendedAll(postfix: Iterable<E>): ImmutableSeq<E> {
    return (this as asia.kala.collection.immutable.ImmutableSeq<E>).appendedAll(postfix)
}

inline fun <E> ImmutableSeq<E>.appendedAll(postfix: Array<out E>): ImmutableSeq<E> {
    return (this as asia.kala.collection.immutable.ImmutableSeq<E>).appendedAll(postfix)
}

inline operator fun <E> ImmutableSeq<E>.plus(element: E): ImmutableSeq<E> {
    return appended(element)
}

inline operator fun <E> ImmutableSeq<E>.plus(elements: Iterable<E>): ImmutableSeq<E> {
    return appendedAll(elements)
}

inline operator fun <E> ImmutableSeq<E>.plus(elements: Array<out E>): ImmutableSeq<E> {
    return appendedAll(elements)
}