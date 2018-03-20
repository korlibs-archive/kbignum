package com.soywiz

val Long.bi get() = BigInt(this)
val Int.bi get() = BigInt(this)
val String.bi get() = BigInt(this)
fun String.bi(radix: Int) = BigInt(this, radix)

val Double.bd get() = BigNum("$this")
val Long.bd get() = BigNum(this.bi, 0)
val Int.bd get() = BigNum(this.bi, 0)
val String.bd get() = BigNum(this)


