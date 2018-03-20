import com.soywiz.*
import org.junit.Test
import java.math.*
import kotlin.test.*

class BigIntCompareWithJVMTest {
	val items = listOf(-9999999, -8888888, -0x10000, -0xFFFF, -100, -50, 0, +50, +100, +0x10000, +0xFFFF, +8888888, +9999999)

	@Test
	fun testSub() = testBinary { jvmL, jvmR, kL, kR -> assertEquals("${ jvmL - jvmR}", "${kL - kR}") }

	@Test
	fun testAdd() = testBinary { jvmL, jvmR, kL, kR -> assertEquals("${jvmL + jvmR}", "${kL + kR}") }

	private fun testBinary(callback: (jvmL: BigInteger, jvmR: BigInteger, kL: BigInt, kR: BigInt) -> Unit) {
		for (l in items) for (r in items) {
			val jvmL = BigInteger("$l")
			val jvmR = BigInteger("$r")

			val kL = l.n
			val kR = r.n
			callback(jvmL, jvmR, kL, kR)
		}
	}
}