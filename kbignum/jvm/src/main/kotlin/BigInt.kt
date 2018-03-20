package com.soywiz

import kotlin.math.*

/**
 * @TODO: Use JVM BigInteger and JS BigInt
 */
class BigInt private constructor(val data: UInt16Array, val signum: Int, var dummy: Boolean) {
	val isSmall get() = data.size <= 1
	val isZero get() = signum == 0
	val isNegative get() = signum < 0
	val isPositive get() = signum > 0
	val isNegativeOrZero get() = signum <= 0
	val isPositiveOrZero get() = signum >= 0
	val maxBits get() = data.size * 16
	val significantBits get() = maxBits - leadingZeros()

	companion object {
		val ZERO = BigInt(uint16ArrayOf(), 0, true)
		val MINUS_ONE = BigInt(uint16ArrayOf(1), -1, true)
		val ONE = BigInt(uint16ArrayOf(1), 1, true)
		val TWO = BigInt(uint16ArrayOf(2), 1, true)
		val TEN = BigInt(uint16ArrayOf(10), 1, true)

		operator fun invoke(data: UInt16Array, signum: Int): BigInt {
			// Trim leading zeros
			var maxN = 0
			for (n in data.size - 1 downTo 0) {
				if (data[n] != 0) {
					maxN = n + 1
					break
				}
			}

			if (maxN == 0) return ZERO
			return BigInt(data.copyOf(maxN), signum, false)
		}

		operator fun invoke(value: Long): BigInt {
			if (value.toInt().toLong() == value) return invoke(value.toInt())
			return invoke("$value")
		}

		private fun create(value: Int): BigInt {
			val magnitude = value.toLong().absoluteValue
			if (value == 0) return BigInt(uint16ArrayOf(), 0, true)
			return BigInt(uint16ArrayOf((magnitude ushr 0).toInt(), (magnitude ushr 16).toInt()), value.sign)
		}

		operator fun invoke(value: Int): BigInt {
			// Optimize by directly using the array
			return when (value) {
				-1 -> MINUS_ONE; 0 -> ZERO; 1 -> ONE; 2 -> TWO
				else -> create(value)
			}
		}

		operator fun invoke(str: String, radix: Int = 10): BigInt {
			if (str == "0") return ZERO
			if (str.startsWith('-')) return -invoke(str.substring(1, radix))
			var out = ZERO
			for (c in str) {
				out *= radix
				out += digit(c)
			}
			return out
		}
	}

	// Optimize
	fun countBits(): Int {
		var out = 0
		for (n in 0 until maxBits) if (getBit(n)) out++
		return out
	}

	fun trailingZeros(): Int {
		if (isZero) return 0
		for (n in 0 until maxBits) if (getBit(n)) return n
		return 0
	}

	fun leadingZeros(): Int {
		if (isZero) return 0
		for (n in 0 until maxBits) if (getBit(maxBits - n - 1)) return n
		return maxBits
	}

	operator fun plus(other: BigInt): BigInt {
		val l = this
		val r = other
		return when {
			l.isZero -> r
			r.isZero -> l
			l.isNegative && r.isPositive -> r - l.absoluteValue
			l.isPositive && r.isNegative -> l - r.absoluteValue
			l.isNegative && r.isNegative -> -(l.absoluteValue + r.absoluteValue)
			else -> BigInt(UnsignedBigInt.add(this.data, other.data), signum)
		}
	}

	operator fun minus(other: BigInt): BigInt {
		val l = this
		val r = other
		return when {
			r.isZero -> l
			l.isZero -> -r
			l.isNegative && r.isNegative -> r.abs() - l.abs() // (-l) - (-r) == (-l) + (r) == (r - l)
			l.isNegative && r.isPositive -> -(l.absoluteValue + r) // -l - r == -(l + r)
			l.isPositive && r.isNegative -> l + r.absoluteValue // l - (-r) == l + r
			l.isPositive && r.isPositive && l < r -> -(r - l)
			else -> BigInt(UnsignedBigInt.sub(l.data, r.data), 1)
		}
	}

	operator fun times(other: BigInt): BigInt {
		if (other == ZERO) return 0.n
		if (other == ONE) return this
		if (other == TWO) return this.shl(1)
		if (other == TEN) return BigInt(UnsignedBigInt.mulSmall(this.data, 10), this.signum)
		if (other.countBits() == 1) return this.shl(other.trailingZeros())
		TODO()
	}

	operator fun div(other: BigInt): BigInt {
		val l = this
		val r = other
		if (other == ZERO) error("Division by zero")
		if (other == ONE) return this
		if (other == TWO) return this.shr(1)
		if (other == TEN) return BigInt(UnsignedBigInt.divRemSmall(this.data, 10).div, this.signum)
		if (other.countBits() == 1) return this.shr(other.trailingZeros())
		TODO()
	}

	operator fun rem(other: BigInt): BigInt {
		if (other == ZERO) error("Division by zero")
		if (other == ONE) return ZERO
		if (other == TWO) return getBitInt(0).n
		if (other == TEN) {
			val res = UnsignedBigInt.divRemSmall(this.data, 10)
			val rem = res.rem
			val remN = rem.n
			return remN
		}
		TODO()
	}

	fun getBitInt(n: Int): Int = ((data[n / 16] ushr (n % 16)) and 1)
	fun getBit(n: Int): Boolean = getBitInt(n) != 0

	infix fun shl(count: Int): BigInt {
		if (count < 0) return this shr (-count)
		var out = this
		var remaining = count
		while (remaining > 0) {
			val bits = min(16, remaining)
			out = out.shlSmall(bits)
			remaining -= bits
		}
		return out
	}

	infix fun shr(count: Int): BigInt {
		if (count < 0) return this shl (-count)
		var out = this
		var remaining = count
		while (remaining > 0) {
			val bits = min(16, remaining)
			out = out.shrSmall(bits)
			remaining -= bits
		}
		return out
	}

	private infix fun shlSmall(count: Int): BigInt {
		val out = UInt16Array(data.size + 1)
		var carry = 0
		val count_rcp = 16 - count
		for (n in 0 until data.size + 1) {
			val v = data[n]
			out[n] = ((carry) or (v shl count))
			carry = v ushr count_rcp
		}
		if (carry != 0) error("ERROR!")
		return BigInt(out, signum)
	}

	private infix fun shrSmall(count: Int): BigInt {
		val out = UInt16Array(data.size)
		var carry = 0
		val count_rcp = 16 - count
		val LOW_MASK = (1 shl count) - 1
		for (n in 0 until data.size) {
			val v = data[n]
			out[n] = ((carry shl count_rcp) and (v ushr count))
			carry = v and LOW_MASK
		}
		return BigInt(out, signum)
	}

	operator fun compareTo(that: BigInt): Int {
		if (this.isNegative && that.isPositiveOrZero) return -1
		if (this.isPositiveOrZero && that.isNegative) return +1
		val resUnsigned = UnsignedBigInt.compare(this.data, that.data)
		return if (this.isNegative && that.isNegative) -resUnsigned else resUnsigned
	}

	override fun hashCode(): Int = this.data.hashCode() * this.signum
	override fun equals(other: Any?): Boolean =
		(other is BigInt) && this.signum == other.signum && this.data.contentEquals(other.data)

	val absoluteValue get() = abs()
	fun abs() = if (this.isZero) ZERO else BigInt(this.data, 1)
	operator fun unaryPlus(): BigInt = this
	operator fun unaryMinus(): BigInt = BigInt(this.data, -signum, false)

	operator fun plus(other: Int): BigInt = plus(other.n)
	operator fun minus(other: Int): BigInt = minus(other.n)
	operator fun times(other: Int): BigInt = times(other.n)
	operator fun times(other: Long): BigInt = times(other.n)
	operator fun div(other: Int): BigInt = div(other.n)
	operator fun rem(other: Int): BigInt = rem(other.n)

	override fun toString() = toString(10)

	fun toString(radix: Int): String {
		return when (radix) {
			2 -> toString2()
			else -> toStringGeneric(radix)
		}
	}

	fun toString2(): String {
		if (this.isNegative) return "-" + this.abs().toString2()
		var out = ""
		for (n in 0 until maxBits) out += if (getBit(n)) '1' else '0'
		out = out.trimEnd('0')
		if (out.isEmpty()) out = "0"
		return out.reversed()
	}

	private fun toStringGeneric(radix: Int): String {
		if (this.isNegative) return "-" + this.abs().toStringGeneric(radix)
		if (this.isZero) return "0"
		var out = ""
		var num = this
		// Optimize with mutable data
		while (num != 0.n) {
			//println(num.toString2())
			val digit = num % radix // Optimize
			out += digit(digit.toInt())
			num /= radix
		}
		return out.reversed()
	}

	fun toInt(): Int {
		if (significantBits > 31) error("Can't represent BigInt as integer")
		val magnitude = (this.data[0].toLong() or (this.data[1].toLong() shl 16)) * signum
		return magnitude.toInt()
	}
}

class UInt16Array private constructor(val data: IntArray) {
	val size get() = data.size

	constructor(size: Int) : this(IntArray(size))

	operator fun get(index: Int) = data.getOrElse(index) { 0 }
	operator fun set(index: Int, value: Int) {
		if (index !in data.indices) return
		data[index] = value and 0xFFFF
	}

	fun contentEquals(other: UInt16Array) = this.data.contentEquals(other.data)
	fun copyOf(size: Int): UInt16Array = UInt16Array(data.copyOf(size))
}

fun uint16ArrayOf(vararg values: Int) = UInt16Array(values.size).apply { for (n in 0 until values.size) this[n] = values[n] }

val Long.n get() = BigInt(this)
val Int.n get() = BigInt(this)
val String.n get() = BigInt(this)
fun String.n(radix: Int) = BigInt(this, radix)

private fun digit(v: Int): Char {
	if (v in 0..9) return '0' + v
	if (v in 10..26) return 'a' + (v - 10)
	error("Invalid digit $v")
}

private fun digit(c: Char): Int {
	return when (c) {
		in '0'..'9' -> c - '0'
		in 'a'..'z' -> c - 'a' + 10
		in 'A'..'Z' -> c - 'A' + 10
		else -> error("Invalid digit '$c'")
	}
}

object UnsignedBigInt {
	internal fun add(l: UInt16Array, r: UInt16Array): UInt16Array {
		var carry = 0
		val out = UInt16Array(max(l.size, r.size) + 1)
		for (i in 0 until out.size) {
			val sum = l[i] + r[i] + carry
			carry = if ((sum ushr 16) != 0) 1 else 0
			out[i] = sum - (carry shl 16)
		}
		return out
	}

	// l >= 0 && r >= 0 && l >= r
	internal fun sub(l: UInt16Array, r: UInt16Array): UInt16Array {
		var borrow = 0
		val out = UInt16Array(max(l.size, r.size) + 1)
		for (i in 0 until out.size) {
			val difference = l[i] - borrow - r[i]
			out[i] = difference
			borrow = if (difference < 0) 1 else 0
		}
		return out
	}

	class DivRemSmall(val div: UInt16Array, val rem: Int)

	fun divRemSmall(value: UInt16Array, r: Int): DivRemSmall {
		val length = value.size
		var rem = 0
		val qq = UInt16Array(value.size)
		for (i in length - 1 downTo 0) {
			val dd = (rem shl 16) + value[i]
			val q = dd / r
			rem = dd - q * r
			qq[i] = q
		}
		return DivRemSmall(qq, rem)
	}

	fun mulSmall(a: UInt16Array, b: Int): UInt16Array {
		val l = a.size
		val out = UInt16Array(l + 1)
		var carry = 0
		var product = 0
		var i = 0
		i = 0
		while (i < l) {
			product = a[i] * b + carry
			carry = (product ushr 16)
			out[i] = product - (carry shl 16)
			i++
		}
		while (carry > 0) {
			out[i++] = carry and 0xFFFF
			carry = (carry ushr 16)
		}
		return out
	}

	fun compare(l: UInt16Array, r: UInt16Array): Int {
		for (n in max(l.size, r.size) - 1 downTo 0) {
			val vl = l[n]
			val vr = r[n]
			if (vl < vr) return -1
			if (vl > vr) return +1
		}
		return 0
	}
}
