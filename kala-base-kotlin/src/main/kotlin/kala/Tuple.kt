@file:Suppress("NOTHING_TO_INLINE")

package kala

typealias Tuple = asia.kala.Tuple
typealias HList<H, T> = asia.kala.HList<out H, out T>

typealias Tuple0 = asia.kala.Tuple0
typealias Tuple1<T1> = asia.kala.Tuple1<out T1>
typealias Tuple2<T1, T2> = asia.kala.Tuple2<out T1, out T2>
typealias Tuple3<T1, T2, T3> = asia.kala.Tuple3<out T1, out T2, out T3>
typealias Tuple4<T1, T2, T3, T4> = asia.kala.Tuple4<out T1, out T2, out T3, out T4>
typealias Tuple5<T1, T2, T3, T4, T5> = asia.kala.Tuple5<out T1, out T2, out T3, out T4, out T5>
typealias Tuple6<T1, T2, T3, T4, T5, T6> = asia.kala.Tuple6<out T1, out T2, out T3, out T4, out T5, out T6>
typealias Tuple7<T1, T2, T3, T4, T5, T6, T7> = asia.kala.Tuple7<out T1, out T2, out T3, out T4, out T5, out T6, out T7>
typealias Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> = asia.kala.Tuple8<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8>
typealias Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = asia.kala.Tuple9<out T1, out T2, out T3, out T4, out T5, out T6, out T7, out T8, out T9>
typealias Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> =
        HList<T1, HList<T2, HList<T3, HList<T4, HList<T5, HList<T6, HList<T7, HList<T8, HList<T9, HList<T10, Tuple0>>>>>>>>>>

typealias Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> =
        HList<T1, HList<T2, HList<T3, HList<T4, HList<T5, HList<T6, HList<T7, HList<T8, HList<T9, HList<T10, HList<T11, Tuple0>>>>>>>>>>>

typealias Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> =
        HList<T1, HList<T2, HList<T3, HList<T4, HList<T5, HList<T6, HList<T7, HList<T8, HList<T9, HList<T10, HList<T11, HList<T12, Tuple0>>>>>>>>>>>>

typealias Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> =
        HList<T1, HList<T2, HList<T3, HList<T4, HList<T5, HList<T6, HList<T7, HList<T8, HList<T9, HList<T10, HList<T11, HList<T12, HList<T13, Tuple0>>>>>>>>>>>>>

typealias Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> =
        HList<T1, HList<T2, HList<T3, HList<T4, HList<T5, HList<T6, HList<T7, HList<T8, HList<T9, HList<T10, HList<T11, HList<T12, HList<T13, HList<T14, Tuple0>>>>>>>>>>>>>>

typealias Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> =
        HList<T1, HList<T2, HList<T3, HList<T4, HList<T5, HList<T6, HList<T7, HList<T8, HList<T9, HList<T10, HList<T11, HList<T12, HList<T13, HList<T14, HList<T15, Tuple0>>>>>>>>>>>>>>>

typealias Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> =
        HList<T1, HList<T2, HList<T3, HList<T4, HList<T5, HList<T6, HList<T7, HList<T8, HList<T9, HList<T10, HList<T11, HList<T12, HList<T13, HList<T14, HList<T15, HList<T16, Tuple0>>>>>>>>>>>>>>>>

typealias Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> =
        HList<T1, HList<T2, HList<T3, HList<T4, HList<T5, HList<T6, HList<T7, HList<T8, HList<T9, HList<T10, HList<T11, HList<T12, HList<T13, HList<T14, HList<T15, HList<T16, HList<T17, Tuple0>>>>>>>>>>>>>>>>>

typealias Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> =
        HList<T1, HList<T2, HList<T3, HList<T4, HList<T5, HList<T6, HList<T7, HList<T8, HList<T9, HList<T10, HList<T11, HList<T12, HList<T13, HList<T14, HList<T15, HList<T16, HList<T17, HList<T18, Tuple0>>>>>>>>>>>>>>>>>>

inline fun tupleOf(): Tuple0 = asia.kala.Tuple0.INSTANCE

inline fun <T1>
        tupleOf(t1: T1): Tuple1<T1> = asia.kala.Tuple1(t1)

inline fun <T1, T2>
        tupleOf(t1: T1, t2: T2): Tuple2<T1, T2> = asia.kala.Tuple2(t1, t2)

inline fun <T1, T2, T3>
        tupleOf(t1: T1, t2: T2, t3: T3): Tuple3<T1, T2, T3> = asia.kala.Tuple3(t1, t2, t3)

inline fun <T1, T2, T3, T4>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4): Tuple4<T1, T2, T3, T4> = asia.kala.Tuple4(t1, t2, t3, t4)

inline fun <T1, T2, T3, T4, T5>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5): Tuple5<T1, T2, T3, T4, T5> = asia.kala.Tuple5(t1, t2, t3, t4, t5)

inline fun <T1, T2, T3, T4, T5, T6>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6): Tuple6<T1, T2, T3, T4, T5, T6> = asia.kala.Tuple6(t1, t2, t3, t4, t5, t6)

inline fun <T1, T2, T3, T4, T5, T6, T7>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7): Tuple7<T1, T2, T3, T4, T5, T6, T7> = asia.kala.Tuple7(t1, t2, t3, t4, t5, t6, t7)

inline fun <T1, T2, T3, T4, T5, T6, T7, T8>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8): Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> = asia.kala.Tuple8(t1, t2, t3, t4, t5, t6, t7, t8)

inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9): Tuple9<T1, T2, T3, T4, T5, T6, T7, T8, T9> = asia.kala.Tuple9(t1, t2, t3, t4, t5, t6, t7, t8, t9)

@JvmName("tupleOf10")
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10): Tuple10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> = asia.kala.Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10)

@JvmName("tupleOf11")
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11): Tuple11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> = asia.kala.Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11)

@JvmName("tupleOf12")
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12): Tuple12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> = asia.kala.Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12)

@JvmName("tupleOf13")
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13): Tuple13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> = asia.kala.Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13)

@JvmName("tupleOf14")
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14): Tuple14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> = asia.kala.Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14)

@JvmName("tupleOf15")
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15): Tuple15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> = asia.kala.Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15)

@JvmName("tupleOf16")
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16): Tuple16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> = asia.kala.Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16)

@JvmName("tupleOf17")
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17): Tuple17<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17> = asia.kala.Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17)

@JvmName("tupleOf18")
inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18>
        tupleOf(t1: T1, t2: T2, t3: T3, t4: T4, t5: T5, t6: T6, t7: T7, t8: T8, t9: T9, t10: T10, t11: T11, t12: T12, t13: T13, t14: T14, t15: T15, t16: T16, t17: T17, t18: T18): Tuple18<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18> = asia.kala.Tuple.of(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18)

inline operator fun <T> HList<T, *>.component1(): T = this.elementAt(0)

inline operator fun <T> HList<*, HList<T, *>>.component2(): T = this.elementAt(1)

inline operator fun <T> HList<*, HList<*, HList<T, *>>>.component3(): T = this.elementAt(2)

inline operator fun <T> HList<*, HList<*, HList<*, HList<T, *>>>>.component4(): T = this.elementAt(3)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>.component5(): T = this.elementAt(4)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>.component6(): T = this.elementAt(5)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>.component7(): T = this.elementAt(6)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>.component8(): T = this.elementAt(7)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>>.component9(): T = this.elementAt(8)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>>>.component10(): T = this.elementAt(9)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>>>>.component11(): T = this.elementAt(10)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>>>>>.component12(): T = this.elementAt(11)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>>>>>>.component13(): T = this.elementAt(12)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>>>>>>>.component14(): T = this.elementAt(13)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>>>>>>>>.component15(): T = this.elementAt(14)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>>>>>>>>>.component16(): T = this.elementAt(15)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>>>>>>>>>>.component17(): T = this.elementAt(16)

inline operator fun <T> HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<*, HList<T, *>>>>>>>>>>>>>>>>>>.component18(): T = this.elementAt(17)

inline fun <T1, T2> Tuple2<T1, T2>.toPair(): Pair<T1, T2> = Pair(this._1, this._2)

inline fun <T1, T2> Pair<T1, T2>.toTuple(): Tuple2<T1, T2> = tupleOf(this.first, this.second)

inline fun <T1, T2, T3> Tuple3<T1, T2, T3>.toTriple(): Triple<T1, T2, T3> = Triple(this._1, this._2, this._3)

inline fun <T1, T2, T3> Triple<T1, T2, T3>.toTuple(): Tuple3<T1, T2, T3> = tupleOf(this.first, this.second, this.third)
