@file:Suppress("UNCHECKED_CAST")

package kala.collection

//
// -- Traversable
//

typealias TraversableOnce<E> = asia.kala.collection.TraversableOnce<out E>
typealias Traversable<E> = asia.kala.collection.Traversable<out E>

inline fun <reified E> TraversableOnce<E>.toTypedArray(): Array<E> {
    return this.toArray { size: Int -> arrayOfNulls<E>(size) as Array<E> }
}


//
// -- Seq
//

typealias Seq<E> = asia.kala.collection.Seq<out E>

typealias IndexedSeq<E> = asia.kala.collection.IndexedSeq<out E>

//
// -- Set
//

typealias Set<E> = asia.kala.collection.Set<E>