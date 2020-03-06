@file:Suppress("NOTHING_TO_INLINE")

package kala

typealias Result<T, E> = asia.kala.Result<out T, out E>

typealias Ok<T> = asia.kala.Result.Ok<out T, Nothing>
typealias Err<E> = asia.kala.Result.Err<Nothing, out E>

inline fun <T> ok(value: T): Ok<T> = Result.ok(value)

inline fun <E> err(value: E): Err<E> = Result.err(value)

inline operator fun <T> Result<T, *>.component1(): T? = this.orNull
inline operator fun <E> Result<*, E>.component2(): E? = this.errOrNull
