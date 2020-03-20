@file:Suppress("NOTHING_TO_INLINE")

package kala


/**
 * A container object which may or may not contain a value.
 */
typealias Option<T> = asia.kala.control.Option<out T>

@Throws(NoSuchElementException::class)
inline operator fun <T> Option<T>.component1(): T {
    return this.get()
}

inline fun <T : Any> optionOf(value: T?): Option<T> = Option.of(value)

inline fun <T> someOf(value: T): Option<T> = Option.some(value)

val none: Option<Nothing> = Option.none()