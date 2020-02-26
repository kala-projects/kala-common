@file:Suppress("NOTHING_TO_INLINE")

package kala.collection.mutable

typealias MSeq<E> = asia.kala.collection.mutable.MSeq<E>

inline fun <E> MutableList<E>.asKala(): MSeq<E> {
    return MSeq.wrapJava(this)
}