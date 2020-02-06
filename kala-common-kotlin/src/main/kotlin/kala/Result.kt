@file:Suppress("NOTHING_TO_INLINE")

package kala

typealias Result<T, E> = asia.kala.Result<out T, out E>

typealias Ok<T> = asia.kala.Result.Ok<out T, Nothing>
typealias Err<E> = asia.kala.Result.Err<Nothing, out E>

inline fun <T> ok(value: T): Ok<T> = asia.kala.Result.ok(value)

inline fun <E> err(value: E): Err<E> = asia.kala.Result.err(value)

inline fun <T, E> Result<T, E>.component1(): T = this.get()
