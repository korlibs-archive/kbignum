import com.soywiz.*
import org.junit.Test
import java.math.*
import kotlin.test.*

class BigIntCompareWithJVMTest {
	val items = listOf(-9999999, -8888888, -0x10001, -0x10000, -0xFFFF, -0xFFFE, -100, -50, 0, +50, +100, +0xFFFE, +0xFFFF, +0x10000, +0x10001, +8888888, +9999999)

	@Test
	fun testSub() = testBinary { jvmL, jvmR, kL, kR -> assertEquals("${ jvmL - jvmR}", "${kL - kR}") }

	@Test
	fun testAdd() = testBinary { jvmL, jvmR, kL, kR -> assertEquals("${jvmL + jvmR}", "${kL + kR}") }

	@Test
	fun testBigBig() {
		val a = "9191291821821972198723892731927412419757607241902412742141904810123913021931"
		val b = "121231246717581291824912849128509185124190310741841824712837131738172"
		assertEquals("${BigInteger(a) + BigInteger(b)}", "${a.bi + b.bi}")
		assertEquals("${BigInteger(a) + -BigInteger(b)}", "${a.bi + -b.bi}")
		assertEquals("${-BigInteger(a) + BigInteger(b)}", "${-a.bi + b.bi}")
		assertEquals("${-BigInteger(a) + -BigInteger(b)}", "${-a.bi + -b.bi}")

		assertEquals("${BigInteger(a) - BigInteger(b)}", "${a.bi - b.bi}")
		assertEquals("${BigInteger(a) - -BigInteger(b)}", "${a.bi - -b.bi}")
		assertEquals("${-BigInteger(a) - BigInteger(b)}", "${-a.bi - b.bi}")
		assertEquals("${-BigInteger(a) - -BigInteger(b)}", "${-a.bi - -b.bi}")

		assertEquals("${BigInteger(a) * BigInteger(b)}", "${a.bi * b.bi}")
		assertEquals("${BigInteger(a) * -BigInteger(b)}", "${a.bi * -b.bi}")
		assertEquals("${-BigInteger(a) * BigInteger(b)}", "${-a.bi * b.bi}")
		assertEquals("${-BigInteger(a) * -BigInteger(b)}", "${-a.bi * -b.bi}")
	}

	@Test
	fun testBigSmall() {
		val a = "123678"
		val b = "456965"
		assertEquals("${BigInteger(a) + BigInteger(b)}", "${a.bi + b.bi}")
		assertEquals("${BigInteger(a) - BigInteger(b)}", "${a.bi - b.bi}")
		assertEquals("${BigInteger(a) * BigInteger(b)}", "${a.bi * b.bi}")
		assertEquals("${BigInteger(a) * -BigInteger(b)}", "${a.bi * -b.bi}")
		assertEquals("${-BigInteger(a) * BigInteger(b)}", "${-a.bi * b.bi}")
		assertEquals("${-BigInteger(a) * -BigInteger(b)}", "${-a.bi * -b.bi}")
	}

	@Test
	fun testBigSmall2() {
		val a = "192318471586571265712651786924871293164197657612641412412410410"
		val b = "1234"
		assertEquals("${BigInteger(a) + BigInteger(b)}", "${a.bi + b.bi}")
		assertEquals("${BigInteger(a) - BigInteger(b)}", "${a.bi - b.bi}")
		assertEquals("${BigInteger(a) * BigInteger(b)}", "${a.bi * b.bi}")
		assertEquals("${BigInteger(a) * -BigInteger(b)}", "${a.bi * -b.bi}")
		assertEquals("${-BigInteger(a) * BigInteger(b)}", "${-a.bi * b.bi}")
		assertEquals("${-BigInteger(a) * -BigInteger(b)}", "${-a.bi * -b.bi}")
	}

	private fun testBinary(callback: (jvmL: BigInteger, jvmR: BigInteger, kL: BigInt, kR: BigInt) -> Unit) {
		for (l in items) for (r in items) {
			val jvmL = BigInteger("$l")
			val jvmR = BigInteger("$r")
			val kL = l.bi
			val kR = r.bi
			callback(jvmL, jvmR, kL, kR)
		}
	}
}