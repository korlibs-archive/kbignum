import kotlin.math.*

/**
 * @TODO: Use JVM BigInteger and JS BigInt
 */
class BigInt private constructor(val data: UInt16Array, val signum: Int) {
	val isZero get() = signum == 0
	val isNegative get() = signum < 0
	val isPositive get() = signum > 0
	val maxBits get() = data.size * 16

	companion object {
		val ZERO = create(0)
		val MINUS_ONE = create(-1)
		val ONE = create(1)
		val TWO = create(2)

		operator fun invoke(value: Long): BigInt {
			if (value.toInt().toLong() == value) return invoke(value.toInt())
			return invoke("$value")
		}

		private fun create(value: Int): BigInt {
			val magnitude = value.toLong() and 0xFFFFFFFFL
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
			for (n in 0 until str.length) {
				val c = str[n]
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

	operator fun plus(other: BigInt): BigInt {
		return when {
			other.isZero -> this
			this.isZero -> other
			this.signum == other.signum -> BigInt(UnsignedBigInt.add(this.data, other.data), signum)
			this.isNegative -> (other - this)
			else -> (this - other)
		}
	}

	operator fun minus(other: BigInt): BigInt {
		TODO()
	}

	operator fun times(other: BigInt): BigInt {
		if (other == 0.n) return 0.n
		if (other == 1.n) return this
		if (other == 2.n) return this.shl(1)
		if (other.countBits() == 1) return this.shl(other.trailingZeros())
		TODO()
	}

	operator fun div(other: BigInt): BigInt {
		if (other == 0.n) error("Division by zero")
		if (other == 1.n) return this
		if (other == 2.n) return this.shr(1)
		if (other.countBits() == 1) return this.shr(other.trailingZeros())
		TODO()
	}

	operator fun rem(other: BigInt): BigInt {
		TODO()
	}

	fun getBit(n: Int): Boolean {
		return ((data[n / 16] ushr (n % 16)) and 1) != 0
	}

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
		val out = UInt16Array(data.size)
		var carry = 0
		val count_rcp = 16 - count
		for (n in 0 until data.size) {
			val v = data[n]
			out[n] = ((carry) or (v shl count))
			carry = v ushr count_rcp
		}
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

	operator fun compareTo(other: BigInt): Int {
		TODO()
	}

	override fun equals(other: Any?): Boolean {
		return (other is BigInt) && this.signum == other.signum && this.data.contentEquals(other.data)
	}

	fun abs() = BigInt(this.data, 0)
	operator fun unaryPlus(): BigInt = this
	operator fun unaryMinus(): BigInt = 0.n - this
	operator fun plus(other: Int): BigInt = plus(other.n)
	operator fun minus(other: Int): BigInt = minus(other.n)
	operator fun times(other: Int): BigInt = times(other.n)
	operator fun div(other: Int): BigInt = div(other.n)
	operator fun rem(other: Int): BigInt = rem(other.n)

	override fun toString() = toString(10)

	fun toString(radix: Int): String {
		return when (radix) {
			2 -> toString2()
			else -> error("Unsupported other radix than 2 at this point")
		}
	}

	fun toString2(): String {
		var out = ""
		for (n in 0 until maxBits) out += if (getBit(n)) '1' else '0'
		out = out.trimEnd('0')
		if (out.isEmpty()) out = "0"
		if (signum < 0) out += "-"
		return out.reversed()
	}
}

class UInt16Array(val size: Int) {
	val data = IntArray(size)
	operator fun get(index: Int) = data.getOrElse(index) { 0 }
	operator fun set(index: Int, value: Int) {
		if (index !in data.indices) return
		data[index] = value and 0xFFFF
	}

	fun contentEquals(other: UInt16Array) = this.data.contentEquals(other.data)
}

fun uint16ArrayOf(vararg values: Int) = UInt16Array(values.size).apply { for (n in 0 until values.size) this[n] = values[n] }

val Long.n get() = BigInt(this)
val Int.n get() = BigInt(this)
val String.n get() = BigInt(this)
fun String.n(radix: Int) = BigInt(this, radix)

private fun digit(c: Char): Int {
	return when (c) {
		in '0'..'9' -> c - '0'
		in 'a'..'z' -> c - 'a' + 10
		in 'A'..'Z' -> c - 'A' + 10
		else -> error("Invalid digit '$c'")
	}
}

class UnsignedBigInt private constructor(val data: UInt16Array) {
	companion object {
		internal fun add(l: UInt16Array, r: UInt16Array, out: UInt16Array = UInt16Array(max(l.size, r.size) + 1)): UInt16Array {
			var carry = 0
			for (n in 0 until max(l.size, r.size)) {
				val lv = l[n]
				val rv = r[n]
				val res = lv + rv + carry
				out[n] = res
				carry = res ushr 16
			}
			return out
		}

		internal fun sub(l: CharArray, r: CharArray, out: CharArray = CharArray(max(l.size, r.size) + 1)): CharArray {
			var carry = 0
			for (n in 0 until max(l.size, r.size)) {
				val lv = l[n]
				val rv = r[n]
				val res = lv - rv - carry
				out[n] = res.toChar()
				carry = if (res < 0) -1 else 0
			}
			return out
		}
	}
}
