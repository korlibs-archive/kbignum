import com.soywiz.*
import kotlin.math.*

class BigNum(val int: BigInt, val scale: Int) {
	init {
		//println("BigNum($int, $scale) == $this")
	}

	companion object {
		operator fun invoke(str: String): BigNum {
			val ss = if (str.contains('.')) str.trimEnd('0') else str
			val point = ss.indexOf('.')
			val int = BigInt(ss.replace(".", ""))
			return if (point < 0) {
				BigNum(int, 0)
			} else {
				BigNum(int, ss.length - point - 1)
			}
		}
	}

	fun convertToScale(otherScale: Int): BigNum {
		if (this.scale == otherScale) {
			return this
		} else if (otherScale > this.scale) {
			val scaleAdd = otherScale - this.scale
			//return BigNum(int * ((10.n) pow scaleAdd), otherScale)
			var out = int
			for (n in 0 until scaleAdd) out *= 10
			return BigNum(out, otherScale)
		} else {
			TODO()
		}
	}

	operator fun plus(other: BigNum): BigNum = binary(other, BigInt::plus)
	operator fun minus(other: BigNum): BigNum = binary(other, BigInt::minus)

	private fun binary(other: BigNum, callback: (l: BigInt, r: BigInt) -> BigInt): BigNum {
		val commonScale = max(this.scale, other.scale)
		return BigNum(callback(this.convertToScale(commonScale).int, other.convertToScale(commonScale).int), commonScale)
	}

	override fun toString(): String {
		//return "BigNum($int, $scale)"
		val out = "$int"
		val pos = out.length - scale
		return if (pos <= 0) {
			"0." + "0".repeat(-pos) + out
		} else {
			(out.substring(0, pos) + "." + out.substring(pos)).trimEnd('.')
		}
	}
}