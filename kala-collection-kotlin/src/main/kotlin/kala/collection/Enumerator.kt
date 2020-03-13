@file:Suppress("NOTHING_TO_INLINE")

package kala.collection

import kotlin.experimental.ExperimentalTypeInference

typealias Enumerator<E> = asia.kala.collection.Enumerator<out E>

@UseExperimental(ExperimentalTypeInference::class)
fun <E> enumerator(@BuilderInference block: suspend SequenceScope<E>.() -> Unit): Enumerator<E> {
    return iterator(block).asKala()
}

inline fun <E> Traversable<E>.enumerator(): Enumerator<E> {
    return this.iterator()
}

inline fun <E> Iterable<E>.enumerator(): Enumerator<E> {
    return Enumerator.fromJava(this.iterator())
}

inline fun <E> Iterator<E>.asKala(): Enumerator<E> {
    return Enumerator.fromJava(this)
}

inline fun <E> Enumerator<E>.asKala(): Enumerator<E> {
    return this
}
