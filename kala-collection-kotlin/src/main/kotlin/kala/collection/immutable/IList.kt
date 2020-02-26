@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kala.collection.immutable

typealias IList<E> = asia.kala.collection.immutable.IList<out E>

inline fun <E> immutableLinkedListOf(): IList<E> {
    return IList.of()
}

inline fun <E> immutableLinkedListOf(vararg elements: E): IList<E> {
    return IList.from(elements)
}

inline fun <E> IList<E>.cons(value: E): IList<E> {
    return (this as asia.kala.collection.immutable.IList<E>).cons(value)
}

inline fun <E> IList<E>.updated(index: Int, newValue: E): IList<E> {
    return (this as asia.kala.collection.immutable.IList<E>).updated(index, newValue)
}

inline fun <E> IList<E>.prepended(value: E): IList<E> {
    return (this as asia.kala.collection.immutable.IList<E>).prepended(value)
}

inline fun <E> IList<E>.prependedAll(prefix: Iterable<E>): IList<E> {
    return (this as asia.kala.collection.immutable.IList<E>).prependedAll(prefix)
}

inline fun <E> IList<E>.prependedAll(prefix: Array<out E>): IList<E> {
    return (this as asia.kala.collection.immutable.IList<E>).prependedAll(prefix)
}

inline fun <E> IList<E>.appended(value: E): IList<E> {
    return (this as asia.kala.collection.immutable.IList<E>).appended(value)
}

inline fun <E> IList<E>.appendedAll(postfix: Iterable<E>): IList<E> {
    return (this as asia.kala.collection.immutable.IList<E>).appendedAll(postfix)
}

inline fun <E> IList<E>.appendedAll(postfix: Array<out E>): IList<E> {
    return (this as asia.kala.collection.immutable.IList<E>).appendedAll(postfix)
}

inline operator fun <E> IList<E>.plus(value: E): IList<E> {
    return appended(value)
}

inline operator fun <E> IList<E>.plus(postfix: Iterable<E>): IList<E> {
    return appendedAll(postfix)
}

inline operator fun <E> IList<E>.plus(postfix: Array<out E>): IList<E> {
    return appendedAll(postfix)
}