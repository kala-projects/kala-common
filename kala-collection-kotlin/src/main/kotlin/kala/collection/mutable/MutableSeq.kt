@file:Suppress("NOTHING_TO_INLINE")

package kala.collection.mutable

typealias MutableSeq<E> = asia.kala.collection.mutable.MutableSeq<E>

inline fun <E> MutableList<E>.asKala(): MutableSeq<E> {
    return MutableSeq.wrapJava(this)
}