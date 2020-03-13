@file:Suppress("NOTHING_TO_INLINE")

package kala

import kotlin.reflect.KProperty

typealias Value<T> = asia.kala.Value<out T>

inline val <T> Value<T>.value: T
    get() = this.get()

inline operator fun <E> Value<E>.getValue(thisRef: Any?, property: KProperty<*>): E {
    return value
}


typealias LazyValue<T> = asia.kala.LazyValue<out T>

inline fun <T> lazyValueOf(value: T): LazyValue<T> {
    return LazyValue.ofValue(value)
}

inline fun <T> lazyValueBy(crossinline supplier: () -> T): LazyValue<T> {
    return LazyValue.of { supplier() }
}

inline fun <T> LazyValue<T>.asKotlin(): Lazy<T> {
    if (this.isReady) {
        return lazyOf(this.value)
    }
    return lazy { this.value }
}

inline fun <T> Lazy<T>.asKala() : LazyValue<T> {
    if(this.isInitialized()) {
        return lazyValueOf(this.value)
    }
    return lazyValueBy { this.value }
}