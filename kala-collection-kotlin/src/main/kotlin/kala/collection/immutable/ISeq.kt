@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package kala.collection.immutable

typealias ISeq<E> = asia.kala.collection.immutable.ISeq<out E>

inline fun <E> ISeq<E>.updated(index: Int, newValue: E): ISeq<E> {
    return (this as asia.kala.collection.immutable.ISeq<E>).updated(index, newValue)
}

inline fun <E> ISeq<E>.prepended(value: E): ISeq<E> {
    return (this as asia.kala.collection.immutable.ISeq<E>).prepended(value)
}

inline fun <E> ISeq<E>.appended(value: E): ISeq<E> {
    return (this as asia.kala.collection.immutable.ISeq<E>).appended(value)
}
