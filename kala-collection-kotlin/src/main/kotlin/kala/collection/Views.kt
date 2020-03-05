@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package kala.collection

typealias View<E> = asia.kala.collection.View<out E>

//
// -- SeqView
//

typealias SeqView<E> = asia.kala.collection.SeqView<out E>

inline fun <E> SeqView<E>.updated(index: Int, newValue: E): SeqView<E> {
    return (this as asia.kala.collection.SeqView<E>).updated(index, newValue)
}

inline fun <E> SeqView<E>.prepended(value: E): SeqView<E> {
    return (this as asia.kala.collection.SeqView<E>).prepended(value)
}

inline fun <E> SeqView<E>.appended(value: E): SeqView<E> {
    return (this as asia.kala.collection.SeqView<E>).appended(value)
}


//
// -- IndexedSeqView
//

typealias IndexedSeqView<E> = asia.kala.collection.IndexedSeqView<out E>

inline fun <E> IndexedSeqView<E>.updated(index: Int, newValue: E): IndexedSeqView<E> {
    return (this as asia.kala.collection.IndexedSeqView<E>).updated(index, newValue)
}

inline fun <E> IndexedSeqView<E>.prepended(value: E): IndexedSeqView<E> {
    return (this as asia.kala.collection.IndexedSeqView<E>).prepended(value)
}

inline fun <E> IndexedSeqView<E>.appended(value: E): IndexedSeqView<E> {
    return (this as asia.kala.collection.IndexedSeqView<E>).appended(value)
}