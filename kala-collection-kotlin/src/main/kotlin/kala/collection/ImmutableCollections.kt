@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package kala.collection

typealias ImmutableCollection<E> = asia.kala.collection.immutable.ImmutableCollection<out E>

//
// -- ImmutableSeq
//

typealias ImmutableSeq<E> = asia.kala.collection.immutable.ImmutableSeq<out E>

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

// -- ImmutableArray

typealias ImmutableArray<E> = asia.kala.collection.immutable.ImmutableArray<out E>

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

// -- ImmutableVector

typealias ImmutableVector<E> = asia.kala.collection.immutable.ImmutableVector<out E>

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

// -- ImmutableList

typealias ImmutableList<E> = asia.kala.collection.immutable.ImmutableList<out E>

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
