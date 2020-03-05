@file:Suppress("NOTHING_TO_INLINE")

package kala.collection

typealias MutableCollection<E> = asia.kala.collection.mutable.MutableCollection<E>

//
// -- MutableSeq
//

typealias MutableSeq<E> = asia.kala.collection.mutable.MutableSeq<E>

inline fun <E> MutableList<E>.asKala(): MutableSeq<E> {
    return MutableSeq.wrapJava(this)
}

// -- MutableArray

typealias MutableArray<E> = asia.kala.collection.mutable.MutableArray<E>

inline fun <E> Array<E>.asKala(): MutableArray<E> {
    return MutableArray.wrap(this)
}

//
// -- Buffer
//

typealias Buffer<E> = asia.kala.collection.mutable.Buffer<E>

inline operator fun <E> Buffer<E>.plusAssign(value: E) {
    this.append(value)
}

inline operator fun <E> Buffer<E>.plusAssign(values: Iterable<E>) {
    this.appendAll(values)
}

inline operator fun <E> Buffer<E>.plusAssign(values: Array<out E>) {
    this.appendAll(values)
}

inline fun <E> MutableList<E>.asKalaBuffer(): Buffer<E> {
    return Buffer.wrapJava(this)
}

// -- ArrayBuffer

typealias ArrayBuffer<E> = asia.kala.collection.mutable.ArrayBuffer<E>

// -- LinkedBuffer

typealias LinkedBuffer<E> = asia.kala.collection.mutable.LinkedBuffer<E>
