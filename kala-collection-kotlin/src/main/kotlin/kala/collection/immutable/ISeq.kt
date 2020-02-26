@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package kala.collection.immutable

typealias ISeq<E> = asia.kala.collection.immutable.ISeq<out E>

inline fun <E> immutableSeqOf(vararg elements: E): ISeq<E> {
    return ISeq.of(*elements)
}

inline fun <E> ISeq<E>.updated(index: Int, newValue: E): ISeq<E> {
    return (this as asia.kala.collection.immutable.ISeq<E>).updated(index, newValue)
}

inline fun <E> ISeq<E>.prepended(value: E): ISeq<E> {
    return (this as asia.kala.collection.immutable.ISeq<E>).prepended(value)
}

inline fun <E> ISeq<E>.prependedAll(prefix: Iterable<E>): ISeq<E> {
    return (this as asia.kala.collection.immutable.ISeq<E>).prependedAll(prefix)
}

inline fun <E> ISeq<E>.prependedAll(prefix: Array<out E>): ISeq<E> {
    return (this as asia.kala.collection.immutable.ISeq<E>).prependedAll(prefix)
}

inline fun <E> ISeq<E>.appended(value: E): ISeq<E> {
    return (this as asia.kala.collection.immutable.ISeq<E>).appended(value)
}

inline fun <E> ISeq<E>.appendedAll(postfix: Iterable<E>): ISeq<E> {
    return (this as asia.kala.collection.immutable.ISeq<E>).appendedAll(postfix)
}

inline fun <E> ISeq<E>.appendedAll(postfix: Array<out E>): ISeq<E> {
    return (this as asia.kala.collection.immutable.ISeq<E>).appendedAll(postfix)
}

inline operator fun <E> ISeq<E>.plus(element: E): ISeq<E> {
    return appended(element)
}

inline operator fun <E> ISeq<E>.plus(elements: Iterable<E>): ISeq<E> {
    return appendedAll(elements)
}

inline operator fun <E> ISeq<E>.plus(elements: Array<out E>): ISeq<E> {
    return appendedAll(elements)
}