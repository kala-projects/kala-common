@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package kala.collection

//
// -- Traversable
//

typealias TraversableOnce<E> = asia.kala.collection.TraversableOnce<out E>
typealias Traversable<E> = asia.kala.collection.Traversable<out E>

inline fun <E> Traversable<E>.asKotlin(): Collection<E> {
    return this.asJava()
}

//
// -- Seq
//

typealias Seq<E> = asia.kala.collection.Seq<out E>

typealias IndexedSeq<E> = asia.kala.collection.IndexedSeq<out E>

inline fun <E> Seq<E>.asKotlin(): List<E> {
    return this.asJava()
}

//
// -- Set
//

typealias Set<E> = asia.kala.collection.Set<E>

inline fun <E> Set<E>.asKotlin(): kotlin.collections.Set<E> {
    return this.asJava()
}